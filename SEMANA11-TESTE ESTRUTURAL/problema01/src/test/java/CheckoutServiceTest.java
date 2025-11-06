import org.example.checkout.*;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CheckoutServiceTest {
    @Test
    public void deveCalcularBasicoSemDescontosEImpostoApenasNaoBook() {
        var couponSvc = new CouponService();
        var shipSvc = new ShippingService();
        var service = new CheckoutService(couponSvc, shipSvc);

        var itens = List.of(
                new Item("BOOK", 100.00, 1),
                new Item("ELETRONICO", 50.00, 2) // tributável
        );

        var res = service.checkout(
                itens,
                CustomerTier.BASIC,
                false,
                "SUL",
                3.0,
                null,
                LocalDate.now(),
                null
        );

        assertEquals(200.00, res.subtotal);          // 100 + (50*2)
        assertEquals(0.00, res.discountValue);
        // imposto 12% sobre parte tributável: 100 (eletrônicos)
        assertEquals(12.00, res.tax);
        // frete SUL com peso 3 → 35
        assertEquals(35.00, res.shipping);
        assertEquals(247.00, res.total);
    }

    private CheckoutService svc() {
        return new CheckoutService(new CouponService(), new ShippingService());
    }

    @Test
    public void deveAplicarDescontoDesc10() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "OUTRO", 1.0, "DESC10", LocalDate.now(), null);

        assertEquals(100.00, res.subtotal);
        assertEquals(10.00, res.discountValue);
        assertEquals(10.80, res.tax);
        assertEquals(40.00, res.shipping);
        assertEquals(140.80, res.total);
    }

    @Test
    public void deveIgnorarCupomDESC20PorMinimo() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 50.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "OUTRO", 1.0, "DESC20", LocalDate.now(), LocalDate.now().plusDays(1));
        assertEquals(50.00, res.subtotal);
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void deveAplicarDESC20QuandoAtendeCondicoes() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 100.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "OUTRO", 1.0, "DESC20", LocalDate.now(), LocalDate.now().plusDays(1));
        assertEquals(20.00, res.discountValue);
    }

    @Test
    public void deveAceitarFRETEGRATISQuandoPesoMenorOuIgual5() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 10.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "NORTE", 2.0, "FRETEGRATIS", LocalDate.now(), null);
        assertEquals(0.00, res.shipping);
    }

    @Test
    public void naoConcedeFreteGratisPorCupomSePesoMaiorQue5() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 10.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "NORTE", 6.0, "FRETEGRATIS", LocalDate.now(), null);
        assertEquals(80.00, res.shipping);
    }

    @Test
    public void deveAplicarLimiteMaximoDesconto30Porcento() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 200.00, 1));

        var res = service.checkout(itens, CustomerTier.GOLD, true, "OUTRO", 1.0, "DESC20", LocalDate.now(), LocalDate.now().plusDays(1));
        assertEquals(60.00, res.discountValue);
    }

    @Test
    public void primeiraCompraSóQuandoSubtotalMaiorIgual50() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 49.99, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, true, "OUTRO", 1.0, null, LocalDate.now(), null);
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void descontoPrimeiraCompraQuandoSubtotalMaiorIgual50() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 50.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, true, "OUTRO", 1.0, null, LocalDate.now(), null);
        assertEquals(2.50, res.discountValue);
    }

    @Test
    public void taxaIsentaParaBOOK() {
        var service = svc();
        var itens = List.of(new Item("BOOK", 100.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUDESTE", 1.0, null, LocalDate.now(), null);
        assertEquals(0.00, res.tax);
    }

    @Test
    public void shippingGratisPorValorSubtotalMaiorIgual300() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 300.00, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, null, LocalDate.now(), null);
        assertEquals(0.00, res.shipping);
    }

    @Test
    public void calculaFretePorRegiaoESegmentosPeso() {
        var service = svc();
        assertEquals(20.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "SUL", 2.0, null, LocalDate.now(), null).shipping);
        assertEquals(35.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "SUL", 3.0, null, LocalDate.now(), null).shipping);
        assertEquals(50.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "SUL", 6.0, null, LocalDate.now(), null).shipping);
        assertEquals(30.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "NORTE", 2.0, null, LocalDate.now(), null).shipping);
        assertEquals(55.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "NORTE", 3.0, null, LocalDate.now(), null).shipping);
        assertEquals(80.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "NORTE", 6.0, null, LocalDate.now(), null).shipping);
        assertEquals(40.0, service.checkout(List.of(new Item("ELETRONICO", 10.0,1)), CustomerTier.BASIC, false, "CENTRO", 1.0, null, LocalDate.now(), null).shipping);
    }

    @Test
    public void itemArgumentValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Item("X", -1.0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Item("X", 1.0, 0));
    }

    @Test
    public void shippingWeightValidation() {
        var service = svc();
        assertThrows(IllegalArgumentException.class, () -> service.checkout(List.of(new Item("ELETRONICO", 1.0,1)), CustomerTier.BASIC, false, "SUL", -0.1, null, LocalDate.now(), null));
    }

    @Test
    public void couponDESC20ExpiradoIgnorado() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 150.0, 1));

        var res = service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, "DESC20", LocalDate.now(), LocalDate.now().minusDays(1));
        assertEquals(0.00, res.discountValue);
    }

    @Test
    public void cupomNuloOuVazioIgnorado() {
        var service = svc();
        var itens = List.of(new Item("ELETRONICO", 100.0, 1));
        assertEquals(0.0, service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, null, LocalDate.now(), null).discountValue);
        assertEquals(0.0, service.checkout(itens, CustomerTier.BASIC, false, "SUL", 1.0, "", LocalDate.now(), null).discountValue);
    }
}