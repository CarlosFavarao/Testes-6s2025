package org.example;

public class CalculadoraDeDescontos {
    public double calcular(double valorTotal) {
        if (valorTotal < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }

        if (valorTotal < 100) {
            return 0.0;
        } else if (valorTotal <= 500) {
            return valorTotal * 0.05;
        } else {
            return valorTotal * 0.10;
        }
    }
}
