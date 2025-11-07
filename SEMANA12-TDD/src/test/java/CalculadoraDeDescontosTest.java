import org.example.CalculadoraDeDescontos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraDeDescontosTest {

    private final CalculadoraDeDescontos calculadora = new CalculadoraDeDescontos();

    @Test
    public void deveRetornarZeroParaValorAbaixoDe100() {
        assertEquals(0.0, calculadora.calcular(50.0));
        assertEquals(0.0, calculadora.calcular(99.99));
    }

    @Test
    public void deveAplicar5PorcentoEntre100e500() {
        assertEquals(5.0, calculadora.calcular(100.0));
        assertEquals(25.0, calculadora.calcular(500.0));
        assertEquals(12.5, calculadora.calcular(250.0));
    }

    @Test
    public void deveAplicar10PorcentoAcimaDe500() {
        assertEquals(50.0, calculadora.calcular(500.01), 00.1);
        assertEquals(100.0, calculadora.calcular(1000.0));
    }

    @Test
    public void deveLancarExcecaoParaValoresNegativos() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(-10.0));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcular(-0.01));
    }
}
