package com.pharmacy.api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.commons.NotFoundException;
import com.pharmacy.api.data.SharedData;
import com.pharmacy.api.model.Pharmacy;
import com.pharmacy.api.model.PharmacyPatch;
import com.pharmacy.api.model.Sale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PharmacyService {

    @Inject
    SharedData sharedData;

    public List<Pharmacy> getPharmacys() {
        System.out.println("Service - Obteniendo todas las farmacias...");
        return sharedData.getPharmacies();
    }
    
    public Pharmacy getPharmacyById(Integer id) {
        return sharedData.getPharmacies().stream()
            .filter(p -> p.getIdPharmacy().equals(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("La farmacia con ID " + id + " no fue encontrada"));
    }

    public List<Sale> getSalesByPharmacy(Integer pharmacyId) {
        System.out.println("Service - Obteniendo ventas para farmacia ID: " + pharmacyId);
        
        // Validar que la farmacia existe
        getPharmacyById(pharmacyId);
        
        List<Sale> result = sharedData.getSales().stream()
            .filter(s -> s.getPharmacyId() != null && s.getPharmacyId().equals(pharmacyId))
            .collect(Collectors.toList());
            
        if (result.isEmpty()) {
            throw new NotFoundException("No se encontraron ventas para la farmacia con ID " + pharmacyId);
        }
        return result;
    }
    
    public Pharmacy createPharmacy(Pharmacy pharmacy) {
        System.out.println("Service - Creando farmacia: " + pharmacy);
        
        // Validaciones de negocio
        if (pharmacy.getIdPharmacy() == null) {
            throw new BadRequestException("El ID de la farmacia es requerido");
        }

        boolean idExists = sharedData.getPharmacies().stream()
        .anyMatch(p -> p.getIdPharmacy().equals(pharmacy.getIdPharmacy()));

        if (idExists) {
            throw new AlreadyExistsException("El ID de la farmacia ya existe: " + pharmacy.getIdPharmacy());
        }

        // Guardar en la base de datos
        Pharmacy newPharmacy = new Pharmacy(
                pharmacy.getIdPharmacy(), 
                pharmacy.getName(), 
                pharmacy.getAddress());
                
        sharedData.getPharmacies().add(newPharmacy);
        return newPharmacy;
    }

    public Pharmacy updatePharmacy(Integer id, Pharmacy pharmacy) {
        System.out.println("Service - Actualizando farmacia ID " + id + ": " + pharmacy);
        
        // Validaciones
        if (pharmacy.getIdPharmacy() == null) {
            throw new BadRequestException("El ID de la farmacia es requerido");
        }
        
        // Buscar si la farmacia existe
        Pharmacy existingPharmacy = getPharmacyById(id);

        // Verificar si el nuevo ID ya existe
        boolean idExists = sharedData.getPharmacies().stream()
        .anyMatch(p -> p.getIdPharmacy().equals(pharmacy.getIdPharmacy()) && !p.getIdPharmacy().equals(id));
        
        if (idExists) {
            throw new AlreadyExistsException("El ID de la farmacia ya existe: " + pharmacy.getIdPharmacy());
        }
        
        // Actualizar la farmacia
        existingPharmacy.setIdPharmacy(pharmacy.getIdPharmacy());
        existingPharmacy.setName(pharmacy.getName());
        existingPharmacy.setAddress(pharmacy.getAddress());

        return existingPharmacy;
    }

    public Pharmacy patchPharmacy(Integer id, PharmacyPatch pharmacyPatch) {
        System.out.println("Service - Actualizando parcialmente farmacia ID " + id + ": " + pharmacyPatch);
        
        // Buscar la farmacia existente
        Pharmacy existingPharmacy = getPharmacyById(id);
        
        // Aplicar los cambios del patch
        if (pharmacyPatch.getName() != null) {
            if (pharmacyPatch.getName().trim().isEmpty()) {
                throw new BadRequestException("El nombre de la farmacia no puede estar vacío");
            }
            existingPharmacy.setName(pharmacyPatch.getName());
        }
        
        if (pharmacyPatch.getAddress() != null) {
            existingPharmacy.setAddress(pharmacyPatch.getAddress());
        }
        
        return existingPharmacy;
    }

    public String deletePharmacy(Integer id) {
        System.out.println("Service - Eliminando farmacia ID: " + id);
        
        // Verificar que existe antes de eliminar
        getPharmacyById(id);
        
        // Validar que no tenga ventas asociadas
        boolean hasSales = sharedData.getSales().stream()
            .anyMatch(s -> s.getPharmacyId() != null && s.getPharmacyId().equals(id));
            
        if (hasSales) {
            throw new BadRequestException("No se puede eliminar la farmacia porque tiene ventas asociadas");
        }
        
        // Eliminar la farmacia
        boolean removed = sharedData.getPharmacies().removeIf(p -> p.getIdPharmacy().equals(id));
        
        if (!removed) {
            throw new NotFoundException("La farmacia con ID " + id + " no fue encontrada");
        }
        
        return "Farmacia con ID " + id + " eliminada correctamente";
    }
}
