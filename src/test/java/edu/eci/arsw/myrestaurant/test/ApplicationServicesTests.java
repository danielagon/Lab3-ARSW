package edu.eci.arsw.myrestaurant.test;

import edu.eci.arsw.myrestaurant.beans.BillCalculator;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ApplicationServicesTests {

    @Autowired
    RestaurantOrderServicesStub ros;

    
    @Test
    public void contextLoads() throws OrderServicesException{        
        
    }
    
    @Test
    public void calcularTotal() throws OrderServicesException{
        int totalCalculado = ros.calculateTableBill(1);
        float totalEsperado = (10000*3)+(10000*3*0.19f)+(3000)+(3000*0.19f)+(1300*4)+(1300*4*0.16f);
        int totalCalculadoManual = (int) totalEsperado;
        org.junit.Assert.assertEquals(totalCalculado,totalCalculadoManual);
    }

}
