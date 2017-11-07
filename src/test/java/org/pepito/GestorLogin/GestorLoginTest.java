package org.pepito.GestorLogin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    when(cuenta.claveCorrecta("1234")).thenReturn(true);
    
    login.acceder("pepe", "1234");
    
    verify(cuenta, never()).bloquearCuenta();
  }
  
}
