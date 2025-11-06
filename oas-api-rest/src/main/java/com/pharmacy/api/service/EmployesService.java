package com.pharmacy.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.model.Employe;
import com.pharmacy.api.model.Employe.TypeEmployeEnum;
import com.pharmacy.api.model.EmployePatch; 

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployesService {

    // 1. Simulación de la base de datos (datos de prueba iniciales)
    private List<Employe> employes = new ArrayList<>(Arrays.asList(
        new Employe()
            .idEmploye(101)
            .idPharmacy(3) 
            .name("Juan")
            .lastName("Perez")
            .phone("4421234567")
            .typeEmploye(TypeEmployeEnum.CAJERO),
            
        new Employe()
            .idEmploye(102)
            .idPharmacy(1)
            .name("Maria")
            .lastName("Gomez")
            .phone("4427654321")
            .typeEmploye(TypeEmployeEnum.DOCTOR),
            
        new Employe()
            .idEmploye(201)
            .idPharmacy(2)
            .name("Carlos")
            .lastName("Hernandez")
            .phone("5551112233")
            .typeEmploye(TypeEmployeEnum.CAJERO)
    ));
    
    // --- LECTURA (GET) ---

    public List<Employe> getEmployes() {
        System.out.println("Service - Buscando todos los empleados...");
        return employes;
    }
    
    public Employe getEmploye(Integer id) {
        System.out.println("Service - Buscando empleado por ID: " + id);
        return employes.stream()
            .filter(e -> e.getIdEmploye().equals(id))
            .findFirst()
            .orElseThrow(() -> new AlreadyExistsException("El empleado con ID " + id + " no fue encontrado."));
    }

    // --- CREACIÓN (POST) ---
    
    public Employe createEmploye(Employe employe) {
        System.out.println("Service - Creando empleado: " + employe);
        
        // 2. Validación de regla de negocio: El ID del empleado no debe existir
        boolean idExists = employes.stream()
            .anyMatch(e -> e.getIdEmploye().equals(employe.getIdEmploye()));
            
        if (idExists) {
            // Lanza una excepción para indicar conflicto (409) o bad request (400)
            throw new AlreadyExistsException("El ID del empleado ya existe: " + employe.getIdEmploye());
        }

        // Simula la "inserción" y devuelve el objeto completo
        employes.add(employe);
        return employe;
    }

    // --- REEMPLAZO TOTAL (PUT) ---

    public Employe updateEmploye(Integer id, Employe employeRequest) {
        System.out.println("Service - Actualizando empleado ID " + id + ": " + employeRequest);
        
        Optional<Employe> existingEmployeOpt = employes.stream()
            .filter(e -> e.getIdEmploye().equals(id))
            .findFirst();
            
        if (existingEmployeOpt.isPresent()) {
            // Actualización (simulando que la DB hace el cambio)
            Employe existingEmploye = existingEmployeOpt.get();
            
            // Reemplazo total de los campos
            existingEmploye.setName(employeRequest.getName());
            existingEmploye.setLastName(employeRequest.getLastName());
            existingEmploye.setPhone(employeRequest.getPhone());
            existingEmploye.setTypeEmploye(employeRequest.getTypeEmploye());
            existingEmploye.setIdPharmacy(employeRequest.getIdPharmacy()); // Actualiza la FK también
            
            return existingEmploye;
        } else {
            // Lanza la excepción sugerida para un 404
            throw new AlreadyExistsException("El empleado con ID " + id + " no fue encontrado para actualizar.");
        }
    }

    // --- ACTUALIZACIÓN PARCIAL (PATCH) ---
    // NOTA: Esta versión asume que tienes una clase EmployePatch.java
    public Employe patchEmploye(Integer id, EmployePatch employePatch) {
        System.out.println("Service - Actualizando parcialmente empleado ID " + id + ": " + employePatch);
        
        Optional<Employe> existingEmployeOpt = employes.stream()
            .filter(e -> e.getIdEmploye().equals(id))
            .findFirst();
            
        if (existingEmployeOpt.isPresent()) {
            Employe existingEmploye = existingEmployeOpt.get();
            
            // Aplicar solo los cambios que no son null
            if (employePatch.getName() != null) {
                existingEmploye.setName(employePatch.getName());
            }
            if (employePatch.getLastName() != null) {
                existingEmploye.setLastName(employePatch.getLastName());
            }
            if (employePatch.getPhone() != null) {
                existingEmploye.setPhone(employePatch.getPhone());
            }

            return existingEmploye;
        } else {
            // Lanza la excepción sugerida para un 404
            throw new AlreadyExistsException("El empleado con ID " + id + " no fue encontrado para actualización parcial.");
        }
    }

    // --- ELIMINACIÓN (DELETE) ---

    public String deleteEmploye(Integer id) {
        System.out.println("Service - Eliminando empleado ID: " + id);
        
        // Retorna true si un elemento fue eliminado
        boolean removed = employes.removeIf(e -> e.getIdEmploye().equals(id));
        
        if (removed) {
            return "Empleado con ID " + id + " eliminado correctamente";
        } else {
            // Lanza la excepción sugerida para un 404
            throw new AlreadyExistsException("El empleado con ID " + id + " no fue encontrado para eliminar.");
        }
    }
}