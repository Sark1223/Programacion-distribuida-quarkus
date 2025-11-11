package com.pharmacy.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.commons.NotFoundException;
import com.pharmacy.api.model.Employee;
import com.pharmacy.api.model.Employee.TypeEmployeeEnum;
import com.pharmacy.api.model.EmployeePatch; 

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployeesService {

    // 1. Simulación de la base de datos (datos de prueba iniciales)
    private List<Employee> employees = new ArrayList<>(Arrays.asList(
        new Employee()
            .idEmployee(101)
            .idPharmacy(3) 
            .name("Juan")
            .lastName("Perez")
            .phone("4421234567")
            .typeEmployee(TypeEmployeeEnum.CAJERO),
            
        new Employee()
            .idEmployee(102)
            .idPharmacy(1)
            .name("Maria")
            .lastName("Gomez")
            .phone("4427654321")
            .typeEmployee(TypeEmployeeEnum.DOCTOR),
            
        new Employee()
            .idEmployee(201)
            .idPharmacy(2)
            .name("Carlos")
            .lastName("Hernandez")
            .phone("5551112233")
            .typeEmployee(TypeEmployeeEnum.CAJERO)
    ));
    
    // --- LECTURA (GET) ---

    public List<Employee> getEmployees() {
        System.out.println("Service - Buscando todos los empleados...");
        return employees;
    }
    
    public Employee getEmployee(Integer id) {
        System.out.println("Service - Buscando empleado por ID: " + id);
        return employees.stream()
            .filter(e -> e.getIdEmployee().equals(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("El empleado con ID " + id + " no fue encontrado."));
    }

    // --- CREACIÓN (POST) ---
    
    public Employee createEmployee(Employee employee) {
        System.out.println("Service - Creando empleado: " + employee);
        
        // 2. Validación de regla de negocio: El ID del empleado no debe existir
        boolean idExists = employees.stream()
            .anyMatch(e -> e.getIdEmployee().equals(employee.getIdEmployee()));
            
        if (idExists) {
            // Lanza una excepción para indicar conflicto (409) o bad request (400)
            throw new AlreadyExistsException("El ID del empleado ya existe: " + employee.getIdEmployee());
        }

        employees.add(employee);
        return employee;
    }

    // --- REEMPLAZO TOTAL (PUT) ---

    public Employee updateEmployee(Integer id, Employee employeeRequest) {
        System.out.println("Service - Actualizando empleado ID " + id + ": " + employeeRequest);
        
        Optional<Employee> existingEmployeeOpt = employees.stream()
            .filter(e -> e.getIdEmployee().equals(id))
            .findFirst();
            
        if (existingEmployeeOpt.isPresent()) {
            // Actualización (simulando que la DB hace el cambio)
            Employee existingEmployee = existingEmployeeOpt.get();
            
            // Reemplazo total de los campos
            existingEmployee.setName(employeeRequest.getName());
            existingEmployee.setLastName(employeeRequest.getLastName());
            existingEmployee.setPhone(employeeRequest.getPhone());
            existingEmployee.setTypeEmployee(employeeRequest.getTypeEmployee());
            existingEmployee.setIdPharmacy(employeeRequest.getIdPharmacy()); 
            
            return existingEmployee;
        } else {
            // Lanza la excepción sugerida para un 404
            throw new NotFoundException("El empleado con ID " + id + " no fue encontrado para actualizar.");
        }
    }

    // --- ACTUALIZACIÓN PARCIAL (PATCH) ---
    public Employee patchEmployee(Integer id, EmployeePatch employeePatch) {
        System.out.println("Service - Actualizando parcialmente empleado ID " + id + ": " + employeePatch);
        
        Optional<Employee> existingEmployeeOpt = employees.stream()
            .filter(e -> e.getIdEmployee().equals(id))
            .findFirst();
            
        if (existingEmployeeOpt.isPresent()) {
            Employee existingEmployee = existingEmployeeOpt.get();
            
            // Aplicar solo los cambios que no son null
            if (employeePatch.getName() != null) {
                existingEmployee.setName(employeePatch.getName());
            }
            if (employeePatch.getLastName() != null) {
                existingEmployee.setLastName(employeePatch.getLastName());
            }
            if (employeePatch.getPhone() != null) {
                existingEmployee.setPhone(employeePatch.getPhone());
            }

            return existingEmployee;
        } else {
            // Lanza la excepción sugerida para un 404
            throw new NotFoundException("El empleado con ID " + id + " no fue encontrado para actualización parcial.");
        }
    }

    // --- ELIMINACIÓN (DELETE) ---

    public String deleteEmployee(Integer id) {
        System.out.println("Service - Eliminando empleado ID: " + id);
        
        // Retorna true si un elemento fue eliminado
        boolean removed = employees.removeIf(e -> e.getIdEmployee().equals(id));
        
        if (removed) {
            return "Empleado con ID " + id + " eliminado correctamente";
        } else {
            // Lanza la excepción sugerida para un 404
            throw new NotFoundException("El empleado con ID " + id + " no fue encontrado para eliminar.");
        }
    }
}
