package org.agarcia.junitapp.ejemplo;

import org.agarcia.junitapp.ejemplo.exceptions.DineroInsuficienteException;
import org.agarcia.junitapp.ejemplo.models.Banco;
import org.agarcia.junitapp.ejemplo.models.Cuenta;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

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
    @Tag("Cuenta")
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
    @Tag("Cuenta")
    @DisplayName("Probando la igualdad de cuentas.")
    void testReferenciaCuenta(){
        Cuenta cuenta1 = new Cuenta("Sara", new BigDecimal("200.12"));
        Cuenta cuenta2 = new Cuenta("Sara", new BigDecimal("200.12"));
//        Cuenta cuenta2 = new Cuenta("Pablo", new BigDecimal("220.12"));
        assertEquals(cuenta2, cuenta1);
    }

    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    @Tag("Cuenta")
    @DisplayName("Extrayendo dinero de la cuenta.")
    void testDebitoCuentaRepetir(){
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }



    @Test
    @Tag("Cuenta")
    @DisplayName("Aumento dinero en cuenta.")
    void testCreditoCuenta(){
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }


    @Test
    @Tag("error")
    @Tag("Banco")
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
    @Tag("Banco")
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
    @Tag("Banco")
//    @Disabled
    @DisplayName("Probando diferencias entre cuentas de bancos.")
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

   @Nested
   @Tag("Entorno")
   @DisplayName("Probando variables de entorno.")
    class variablesAmbienteTest{
       @Test
       void imprimirVariablesAmbiente(){
           Map<String, String> getEnv = System.getenv();
           getEnv.forEach((k, v) -> System.out.println(k + " = " + v));
       }

       @Test
       @Tag("Entorno")
       @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "C:\\Program Files\\Java\\jdk-14.0.2")
       void testJavaHome(){

       }

       @Test
       @Tag("Entorno")
       @DisplayName("Comprobacion de entorno.")
       void testEnvironment(){
           boolean esDev = "dev".equals(System.getProperty("ENV"));
           assumeTrue(esDev);

       }
   }

   @Nested
   @DisplayName("Todas las pruebas parametrizadas")
   class pruebasParametrizadasTest{
       @ParameterizedTest
       @ValueSource(strings = {"100", "200", "300", "400", "500"})
       void testDebitoCuenta(String monto){
           cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
       }

       @ParameterizedTest
       @CsvSource({"1, 100", "2, 200", "3, 300", "4, 400", "5, 500"})
       void testDebitoCuentaValueSource(String monto){
           cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
       }

       @ParameterizedTest (name = "numero {index} ejecutando con valor {0} - {arguments}")
       @CsvFileSource(resources = "/data.csv")
       void testDebitoCuentaCsvFileSource(String monto){
           cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
       }

       @Disabled
       @ParameterizedTest (name = "numero {index} ejecutando con valor {0} - {arguments}")
       @MethodSource("montoList")
       void testDebitoCuentaMethodSource(String monto){
           cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
       }

//       static List<String> montoList(){
//           return Arrays.asList("100", "200", "300", "400", "500");
//       }

       @ParameterizedTest
       @CsvSource({"200, 100, John, Asier", "400, 200, Asier, Asier", "300, 100, John, john",  "388, 344, Andrea, Andrea"})
       void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual){
           cuenta.setSaldo(new BigDecimal(saldo));
           cuenta.debito(new BigDecimal(monto));
           cuenta.setPersona(actual);

           assertNotNull(cuenta.getSaldo());
           assertNotNull(cuenta.getPersona());
           assertEquals(esperado, actual);
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
       }
   }

   @Nested
   class pruebasTimeOut{
       @Test
       @Tag("Timeout")
       @Timeout(5)
       void testTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6);
        }

       @Test
       @Tag("Timeout")
       @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
       void testTimeOut1() throws InterruptedException {
           TimeUnit.SECONDS.sleep(6);
       }

       @Test
       @Tag("Timeout")
       void testTimeOutAssertions() {
           assertTimeout(Duration.ofSeconds(5), () -> {
               TimeUnit.MILLISECONDS.sleep(3500);
           });
       }
   }

}