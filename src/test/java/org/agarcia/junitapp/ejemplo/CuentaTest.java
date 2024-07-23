package org.agarcia.junitapp.ejemplo;

import org.agarcia.junitapp.ejemplo.exceptions.DineroInsuficienteException;
import org.agarcia.junitapp.ejemplo.models.Banco;
import org.agarcia.junitapp.ejemplo.models.Cuenta;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        System.out.println("Iniciando metodo");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizado metodo");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando la clase test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando la clase test");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta.")
    void testNombreCuenta(){
        Cuenta cuenta = new Cuenta("Andres");
        cuenta.setPersona("Andres");
        String expected = "Andres";
        String actual = cuenta.getPersona();
        assertEquals(expected, actual);

        assertNotNull(expected, () -> "la cuenta no puede ser nula");

    }
    @Test
    @DisplayName("Probando la igualdad de cuentas.")
    void testReferenciaCuenta(){
        Cuenta cuenta1 = new Cuenta("Sara", new BigDecimal("200.12"));
        Cuenta cuenta2 = new Cuenta("Sara", new BigDecimal("200.12"));
//        Cuenta cuenta2 = new Cuenta("Pablo", new BigDecimal("220.12"));
        assertEquals(cuenta2, cuenta1);
    }

    @Test
    @DisplayName("Extrayendo dinero de la cuenta.")
    void testDebitoCuenta(){
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Aumento dinero en cuenta.")
    void testCreditoCuenta(){
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando la excepcion de dinero insuficiente de la cuenta.")
    void dineroInsuficienteException(){
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Probando la tranferencia de dinero entre cuentas.")
    void testTransferirDineroCuentas(){
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Asier", new BigDecimal("1500"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.tranferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
//    @Disabled
    @DisplayName("Probando diferencias entre cuentas.")
    void testRelacionBancoCuentas(){
//        fail();
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Asier", new BigDecimal("1500"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.tranferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000", cuenta2.getSaldo().toPlainString()),
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Asier")))
        );



    }

}