package com.pharmacy.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.commons.NotFoundException;
import com.pharmacy.api.model.Pharmacy;
import com.pharmacy.api.model.PharmacyPatch;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PharmacyService {

    private List<Pharmacy> pharmacys = new ArrayList<>(Arrays.asList(
        new Pharmacy(1, "Similares - Real Solare", "Avenida Siempre Viva 123 colonia Real Solare"),
        new Pharmacy(12, "Similares 3", "Centro 123"),
        new Pharmacy(32, "Similares Dentista", "Centro 123"),
        new Pharmacy(55, "Similares Veterinaria", "Colonia Las Flores 45 calle 5 cp 78234")
    ));

    public List<Pharmacy> getPharmacys() {
        System.out.println("Service - Obteniendo todas las farmacias...");
        return pharmacys;
    }
    
    public Pharmacy getPharmacyById(Integer id) {
        return pharmacys.stream()
            .filter(p -> p.getIdPharmacy().equals(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("La farmacia con ID " + id + " no fue encontrada"));
    }
    
    public Pharmacy createPharmacy(Pharmacy pharmacy) {
        System.out.println("Service - Creando farmacia: " + pharmacy);
        
        // Validaciones de negocio
        if (pharmacy.getIdPharmacy() == null) {
            throw new BadRequestException("El ID de la farmacia es requerido");
        }
                
        boolean idExists = pharmacys.stream()
                .anyMatch(p -> p.getIdPharmacy().equals(pharmacy.getIdPharmacy()));
        if (idExists) {
            throw new AlreadyExistsException("El ID de la farmacia ya existe: " + pharmacy.getIdPharmacy());
        }

        // Guardar en la base de datos
        Pharmacy newPharmacy = new Pharmacy(
                pharmacy.getIdPharmacy(), 
                pharmacy.getName(), 
                pharmacy.getAddress());
                
        pharmacys.add(newPharmacy);
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

        // Verificar si el nuevo ID ya existe (para otro registro)
        boolean idExists = pharmacys.stream()
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
        
        // Buscar si la farmacia existe y eliminarla
        boolean removed = pharmacys.removeIf(p -> p.getIdPharmacy().equals(id));
        
        if (!removed) {
            throw new NotFoundException("La farmacia con ID " + id + " no fue encontrada");
        }
        
        return "Farmacia con ID " + id + " eliminada correctamente";
    }
}
