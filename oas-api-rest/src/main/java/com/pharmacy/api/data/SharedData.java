package com.pharmacy.api.data;

import com.pharmacy.api.model.Pharmacy;
import com.pharmacy.api.model.Sale;
import com.pharmacy.api.model.SaleProduct;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class SharedData {
    
    private List<Pharmacy> pharmacies = new CopyOnWriteArrayList<>(Arrays.asList(
        new Pharmacy(1, "Similares - Real Solare", "Avenida Siempre Viva 123 colonia Real Solare"),
        new Pharmacy(12, "Similares 3", "Centro 123"),
        new Pharmacy(32, "Similares Dentista", "Centro 123"),
        new Pharmacy(55, "Similares Veterinaria", "Colonia Las Flores 45 calle 5 cp 78234")
    ));
    
    private List<Sale> sales = new CopyOnWriteArrayList<>(Arrays.asList(
        new Sale().saleId(201)
            .saleDate(OffsetDateTime.now())
            .subTotal(1500.50)
            .iva(240.08)
            .total(1740.58)
            .pharmacyId(1)  // Ahora referencia una farmacia existente
            .employeeId(401)
            .products(new ArrayList<SaleProduct>() {{
                add(new SaleProduct().saleId(201).productId(301).quantity(3));
                add(new SaleProduct().saleId(201).productId(302).quantity(1));
            }}),
        new Sale().saleId(202)
            .saleDate(OffsetDateTime.now())
            .subTotal(1000.00)
            .iva(160.00)
            .total(1160.00)
            .pharmacyId(12)  // Ahora referencia una farmacia existente
            .employeeId(402)
            .products(new ArrayList<SaleProduct>() {{
                add(new SaleProduct().saleId(202).productId(401).quantity(2));
            }})
    ));
    
    public List<Pharmacy> getPharmacies() {
        return pharmacies;
    }
    
    public List<Sale> getSales() {
        return sales;
    }
}