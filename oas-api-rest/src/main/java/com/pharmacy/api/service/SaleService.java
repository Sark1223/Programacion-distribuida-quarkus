package com.pharmacy.api.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.commons.NotFoundException;
import com.pharmacy.api.model.Sale;
import com.pharmacy.api.model.SalePatch;
import com.pharmacy.api.model.SaleProduct;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SaleService {

	private List<Sale> sales = new ArrayList<>(Arrays.asList(
		// sample sale 1 (distinct example)
		new Sale().saleId(201)
			.saleDate(OffsetDateTime.now())
			.subTotal(1500.50)
			.iva(240.08)
			.total(1740.58)
			.pharmacyId(301)
			.employeId(401)
			.products(new ArrayList<SaleProduct>() {{
				add(new SaleProduct().saleId(201).productId(301).quantity(3));
				add(new SaleProduct().saleId(201).productId(302).quantity(1));
			}}),
		// sample sale 2 (different example)
		new Sale().saleId(202)
			.saleDate(OffsetDateTime.now())
			.subTotal(1000.00)
			.iva(160.00)
			.total(1160.00)
			.pharmacyId(302)
			.employeId(402)
			.products(new ArrayList<SaleProduct>() {{
				add(new SaleProduct().saleId(202).productId(401).quantity(2));
			}})
	));

	public List<Sale> getSales() {
		System.out.println("Service - Obteniendo todas las ventas...");
		return sales;
	}

	public List<Sale> getSalesByPharmacy(Integer pharmacyId) {
		System.out.println("Service - Obteniendo ventas para farmacia ID: " + pharmacyId);
		List<Sale> result = new ArrayList<>();
		for (Sale s : sales) {
			if (s.getPharmacyId() != null && s.getPharmacyId().equals(pharmacyId)) {
				result.add(s);
			}
		}
		if (result.isEmpty()) {
			throw new NotFoundException("No se encontraron ventas para la farmacia con ID " + pharmacyId);
		}
		return result;
	}

	public Sale getSaleById(Integer id) {
		return sales.stream()
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
		if (sale.getEmployeId() == null) {
			throw new BadRequestException("El ID del empleado es requerido");
		}
		if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
			throw new BadRequestException("Se requiere al menos un producto en la venta");
		}

		// Generar un nuevo ID
		Integer newId = sales.stream()
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
		newSale.setEmployeId(sale.getEmployeId());
		newSale.setProducts(new ArrayList<>(sale.getProducts()));
		
		// Actualizar el saleId en los productos
		newSale.getProducts().forEach(product -> product.setSaleId(newId));

		sales.add(newSale);
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
		if (sale.getEmployeId() == null) {
			throw new BadRequestException("El ID del empleado es requerido");
		}
		if (sale.getProducts() == null || sale.getProducts().isEmpty()) {
			throw new BadRequestException("Se requiere al menos un producto en la venta");
		}

		Sale existing = getSaleById(id);

		// No actualizamos el saleId ya que debe mantenerse el original
		existing.setSaleDate(sale.getSaleDate());
		existing.setSubTotal(sale.getSubTotal());
		existing.setIva(sale.getIva());
		existing.setTotal(sale.getTotal());
		existing.setPharmacyId(sale.getPharmacyId());
		existing.setEmployeId(sale.getEmployeId());
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
		// Los campos pharmacyId y employeId no son modificables en una actualización parcial
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

		boolean removed = sales.removeIf(s -> s.getSaleId().equals(id));

		if (!removed) {
			throw new NotFoundException("La venta con ID " + id + " no fue encontrada");
		}

		return "Venta con ID " + id + " eliminada correctamente";
	}
}
