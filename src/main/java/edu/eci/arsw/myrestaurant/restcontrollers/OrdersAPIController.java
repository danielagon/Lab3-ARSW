/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */

@Service
@RestController
@RequestMapping(value = "/orders")
public class OrdersAPIController {
    
    @Autowired
    private RestaurantOrderServices orders;
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetRecursoOrders(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(orders);
            return new ResponseEntity<>(json,HttpStatus.ACCEPTED);
        }catch (JsonProcessingException ex){
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error creando el json",HttpStatus.NO_CONTENT);
        }
    }
    
    @RequestMapping(value = "/{idmesa}")
    public ResponseEntity<?> manejadorGetOrder(@PathVariable int idmesa){
        try{
            Order order = orders.getTableOrder(idmesa);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(order);
            return new ResponseEntity<>(json,HttpStatus.ACCEPTED);
        }catch(NullPointerException ex){
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("El numero de la mesa no existe o no tiene una orden asociada",HttpStatus.NOT_FOUND);
        }catch(JsonProcessingException ex){
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error creando el json",HttpStatus.NO_CONTENT);
        }
    }
    
    @RequestMapping(method = RequestMethod.POST)	
    public ResponseEntity<?> manejadorPostRecursoOrders(@RequestBody String o){
        try {
            //registrar dato
            ObjectMapper mapper = new ObjectMapper();
            Order newOrder = mapper.readValue(o, Order.class);
            orders.addNewOrderToTable(newOrder);
            return new ResponseEntity<>(HttpStatus.CREATED);           
	} catch (OrderServicesException ex) {        
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error en la nueva orden",HttpStatus.FORBIDDEN);
        } catch (IOException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error al crear la nueva orden",HttpStatus.FORBIDDEN);
        }        
    }
    
    @RequestMapping(value = "/{idmesa}/total")
    public ResponseEntity<?> manejadorGetTotal(@PathVariable int idmesa){
        try{
            int total = orders.calculateTableBill(idmesa);
            return new ResponseEntity<>(total,HttpStatus.ACCEPTED);
        }catch (OrderServicesException ex){
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error en la orden",HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(value = "/{idmesa}",method = RequestMethod.PUT)
    public ResponseEntity<?> manejadorPutRecursoOrders(@RequestBody String p, @PathVariable int idmesa){
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Integer> products = mapper.readValue(p, ConcurrentHashMap.class);
            Order order = orders.getTableOrder(idmesa);
            for (String i:products.keySet()){
                order.addDish(i, products.get(i));
            }
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (IOException ex){
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error ingresando el nuevo producto",HttpStatus.NO_CONTENT);
        }
    }
    
    @RequestMapping(value = "/{idmesa}", method = RequestMethod.DELETE)
    public ResponseEntity<?> manejadorDeleteOrders(@PathVariable int idmesa){
        try{
            orders.releaseTable(idmesa);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (OrderServicesException ex) {
            Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("El numero de la mesa no existe o no tiene una orden asociada",HttpStatus.NOT_FOUND);
        }
    }
}
