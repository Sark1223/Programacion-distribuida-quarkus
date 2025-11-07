package com.pharmacy.api.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.commons.NotFoundException;
import com.pharmacy.api.data.SharedData;
import com.pharmacy.api.model.Sale;
import com.pharmacy.api.model.SalePatch;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SaleService {

	@Inject
    SharedData sharedData;
    
    @Inject
    PharmacyService pharmacyService;

    public List<Sale> getSales() {
        System.out.println("Service - Obteniendo todas las ventas...");
        return sharedData.getSales();
    }

	public Sale getSaleById(Integer id) {
        return sharedData.getSales().stream()
            .filter(s -> s.getSaleId().equals(id))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("La venta con ID " + id + " no fue encontrada"));
    }

    public Sale createSale(Sale sale) {
        System.out.println("Service - Creando venta: " + sale);

		// Validar campos requeridos
		if (sale.getSaleDate() == null) {
			throw new BadRequestException("La fecha de venta es requerida");
		}
		if (sale.getSubTotal() == null) {
			throw new BadRequestException("El subtotal es requerido");
		}
		if (sale.getIva() == null) {
			throw new BadRequestException("El IVA es requerido");
		}
		if (sale.getTotal() == null) {
			throw new BadRequestException("El total es requerido");
		}
		if (sale.getPharmacyId() == null) {
			throw new BadRequestException("El ID de la farmacia es requerido");
		}
		if (sale.getEmployeeId() == null) {
			throw new BadRequestException("El ID del empleado es requerido");
		}
		if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
			throw new BadRequestException("Se requiere al menos un producto en la venta");
		}

		if (sale.getPharmacyId() != null) {
            pharmacyService.getPharmacyById(sale.getPharmacyId());
        }

        // Generar un nuevo ID
        Integer newId = sharedData.getSales().stream()
                .mapToInt(Sale::getSaleId)
                .max()
                .orElse(0) + 1;

        Sale newSale = new Sale();
        newSale.setSaleId(newId);
        newSale.setSaleDate(sale.getSaleDate() != null ? sale.getSaleDate() : OffsetDateTime.now());
        newSale.setSubTotal(sale.getSubTotal());
        newSale.setIva(sale.getIva());
        newSale.setTotal(sale.getTotal());
        newSale.setPharmacyId(sale.getPharmacyId());
        newSale.setEmployeeId(sale.getEmployeeId());
        newSale.setProducts(new ArrayList<>(sale.getProducts()));
        
        // Actualizar el saleId en los productos
        newSale.getProducts().forEach(product -> product.setSaleId(newId));

        sharedData.getSales().add(newSale);
        return newSale;
    }

    public Sale updateSale(Integer id, Sale sale) {
		System.out.println("Service - Actualizando venta ID " + id + ": " + sale);

		// Validar campos requeridos
		if (sale.getSubTotal() == null) {
			throw new BadRequestException("El subtotal es requerido");
		}
		if (sale.getIva() == null) {
			throw new BadRequestException("El IVA es requerido");
		}
		if (sale.getTotal() == null) {
			throw new BadRequestException("El total es requerido");
		}
		if (sale.getPharmacyId() == null) {
			throw new BadRequestException("El ID de la farmacia es requerido");
		}
		if (sale.getEmployeeId() == null) {
			throw new BadRequestException("El ID del empleado es requerido");
		}
		if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
			throw new BadRequestException("Se requiere al menos un producto en la venta");
		}

		if (sale.getPharmacyId() != null) {
            pharmacyService.getPharmacyById(sale.getPharmacyId());
        }

        Sale existing = getSaleById(id);

		// No actualizamos el saleId ya que debe mantenerse el original
		existing.setSaleDate(sale.getSaleDate());
		existing.setSubTotal(sale.getSubTotal());
		existing.setIva(sale.getIva());
		existing.setTotal(sale.getTotal());
		existing.setPharmacyId(sale.getPharmacyId());
		existing.setEmployeeId(sale.getEmployeeId());
		existing.setProducts(new ArrayList<>(sale.getProducts()));
		
		// Aseguramos que los productos mantengan el ID de la venta original
		existing.getProducts().forEach(product -> product.setSaleId(id));

		return existing;
	}

	public Sale patchSale(Integer id, SalePatch salePatch) {
		System.out.println("Service - Actualizando parcialmente venta ID " + id + ": " + salePatch);

		Sale existing = getSaleById(id);

		if (salePatch.getSaleDate() != null) {
			existing.setSaleDate(salePatch.getSaleDate());
		}
		if (salePatch.getSubTotal() != null) {
			if (salePatch.getSubTotal() < 0) {
				throw new BadRequestException("El subtotal no puede ser negativo");
			}
			existing.setSubTotal(salePatch.getSubTotal());
		}
		if (salePatch.getIva() != null) {
			if (salePatch.getIva() < 0) {
				throw new BadRequestException("El IVA no puede ser negativo");
			}
			existing.setIva(salePatch.getIva());
		}
		if (salePatch.getTotal() != null) {
			if (salePatch.getTotal() < 0) {
				throw new BadRequestException("El total no puede ser negativo");
			}
			existing.setTotal(salePatch.getTotal());
		}
		// Los campos pharmacyId y employeeId no son modificables en una actualización parcial
		if (salePatch.getProducts() != null) {
			if (salePatch.getProducts().isEmpty()) {
				throw new BadRequestException("La lista de productos no puede estar vacía");
			}
			existing.setProducts(new ArrayList<>(salePatch.getProducts()));
			// Actualizar el saleId en los productos
			existing.getProducts().forEach(product -> product.setSaleId(id));
		}

		return existing;
	}

	public String deleteSale(Integer id) {
		System.out.println("Service - Eliminando venta ID: " + id);

		// Verificar que existe antes de eliminar
		getSaleById(id);

        boolean removed = sharedData.getSales().removeIf(s -> s.getSaleId().equals(id));

        if (!removed) {
            throw new NotFoundException("La venta con ID " + id + " no fue encontrada");
        }

        return "Venta con ID " + id + " eliminada correctamente";
    }
}
