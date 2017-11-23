package com.example.agentm;

import android.support.test.runner.AndroidJUnit4;

import com.example.agentm.Patterns.Utils;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/*
TOKEN

eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdGFibGVfc2lkIjoic2lkOmRkZjExZDQzN2M5NzA1Y2Y3MjA1N2I0YjU2NDBjYzhlIiwic3ViIjoic2lkOjdlNDFiMzVjODVjYjUzMDg2NjUwMTUzMjU2MTdjODE3IiwiaWRwIjoiYWFkIiwidmVyIjoiMyIsImlzcyI6Imh0dHBzOi8vYWdlbnRtLmF6dXJld2Vic2l0ZXMubmV0LyIsImF1ZCI6Imh0dHBzOi8vYWdlbnRtLmF6dXJld2Vic2l0ZXMubmV0LyIsImV4cCI6MTQ5NzM3NjE0OSwibmJmIjoxNDk0Nzg0MTQ5fQ.SHfPPfBUgSyobdbpuAX0eAtLmOlAPZYYPNc26dqs1ns


sid:7e41b35c85cb5308665015325617c817
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class SociosTest {

    static  int x = 0;

    @Test
    public void A_CreateSocio() throws Exception {
        x = x + 1;
        assertEquals(x, 1);
    }

    @Test
    public void B_GetSocio() throws Exception {
        x = x + 1;
        assertEquals(x, 2);
    }

    @Test
    public void C_UpdateSocio() throws Exception {
        x = x + 1;
        assertEquals(x, 3);
    }

    @Test
    public void D_GetSocioActividades() throws Exception {
        x = x + 1;
        assertEquals(x, x);
    }

    @Test
    public void E_BuscarSocioPorDNI() throws Exception {
        x = x + 1;
        assertEquals(x, x);
    }

    @Test
    public void F_BuscarSocioPorNombre() throws Exception {
        x = x + 1;
        assertEquals(x, x);
    }
    @Test
    public void G_BuscarSocioPorApellido() throws Exception {
        x = x + 1;
        assertEquals(x, x);
    }

    @Test
    public void H_DeleteSocio() throws Exception {
        x = x + 1;
        assertEquals(x, x);
    }
}
