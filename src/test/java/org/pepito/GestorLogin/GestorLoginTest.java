package org.pepito.GestorLogin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class GestorLoginTest {
  GestorLogin login;
  IRepositorioCuentas repo;
  ICuenta cuenta;

  @Before
  public void setUp() throws Exception {
    repo = mock(IRepositorioCuentas.class);
    cuenta = mock(ICuenta.class);
    when(repo.buscar("pepe")).thenReturn(cuenta);
    login = new GestorLogin(repo);
  }

  @After
  public void tearDown() throws Exception {
    repo = null;
    cuenta = null;
    login = null;
  }

  @Test
  public void testAccesoConcedidoALaPrimera() {
    when(cuenta.claveCorrecta("1234")).thenReturn(true);

    login.acceder("pepe", "1234");

    verify(cuenta, times(1)).entrarCuenta();
    verify(cuenta, never()).bloquearCuenta();
  }

  @Test
  public void testSeDeniegaAccesoALaPrimeraVez() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");

    verify(cuenta, never()).entrarCuenta();
  }

  @Test
  public void testUsuarioDesconocido() {
    when(repo.buscar("manolo")).thenThrow(ExcepcionUsuarioDesconocido.class);

    try {
      login.acceder("manolo", anyString());
      fail("Debe lanzar una excepcion");
    } catch (ExcepcionUsuarioDesconocido e) {
      verify(repo).buscar("manolo");
    }


  }

  @Test
  public void siFallaPrimaVez_AccesoNoConcedido_NoBloquear() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");

    verify(cuenta, never()).entrarCuenta();
    verify(cuenta, never()).bloquearCuenta();
    assertThat(login.getNumFallos(), is(1));
  }

  @Test
  public void seBloqueaCuentaDespuesTresIntentos() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");

    assertThat(login.getNumFallos(), is(3));
    verify(cuenta, times(1)).bloquearCuenta();
  }

  @Test
  public void sePuedeAccederTrasUnFallo() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");

    assertThat(login.getNumFallos(), is(1));
    verify(cuenta, never()).entrarCuenta();
  }

  @Test
  public void sePuedeAccederTrasDosFallos() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");

    assertThat(login.getNumFallos(), is(2));
    verify(cuenta, never()).entrarCuenta();
  }

  @Test
  public void seBloqueaLaCuentaDespuesCuatroIntentos() {
    when(cuenta.claveCorrecta("12345")).thenReturn(false);

    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");
    login.acceder("pepe", "12345");

    assertThat(login.getNumFallos(), is(4));
    verify(cuenta, times(1)).bloquearCuenta();
  }

  @Test
  public void otroUsuarioPuedeAccederTrasBloqueo() {
    ICuenta cuenta1 = mock(ICuenta.class);
    when(cuenta.estaBloqueada()).thenReturn(true);
    when(cuenta1.claveCorrecta("12345")).thenReturn(true);
    when(repo.buscar("carlos")).thenReturn(cuenta1);

    login.acceder("carlos", "12345");

    verify(cuenta1, times(1)).entrarCuenta();
    verify(cuenta1, never()).bloquearCuenta();
  }
  
  @Test
  public void siEstaBloqueada_SeDeniegaAcceso() {
    when(cuenta.estaBloqueada()).thenReturn(true);
    
    login.acceder("pepe", anyString());
    
    verify(cuenta, never()).entrarCuenta();
  }

  @Test(expected = ExcepcionCuentaEnUso.class)
  public void seDeniegaAccesoCuentasEnUso() {
    when(cuenta.estaEnUso()).thenReturn(true);

    login.acceder("pepe", "12345");
    
    verify(cuenta).estaEnUso();

  }
}
