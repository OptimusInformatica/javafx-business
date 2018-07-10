/*
 * Copyright 2018 Optimus Informatica <https://optimusinformatica.com.br>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.optimus.business;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * Classe de controle do form principal
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 */
public class BusinessMain implements Initializable {
    /** Ponteiro de log */
    private static final Logger logger = LogManager.getLogger(Business.class);
    /** AncorPane principal */
    @FXML
    private AnchorPane rootPane;
    
    /**
     * Inicializa o form principal
     * @param url arquivo fxml
     * @param rb resourse
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    /**
     * Carrega no AnchorPane o form de registros de empresas
     * @param e evento
     */
    @FXML
    private void loadFormEmpresas(ActionEvent e) {
        try {
            Node node = Business.loadFXML("empresas/FormEmpresas.fxml");
            rootPane.getChildren().clear();
            rootPane.getChildren().add(node);
        }
        catch (IOException ex) {
        	logger.warn(ex.getMessage());
        }
    }
    
    /**
     * Carrega no AnchorPane o form de registros de clientes
     * @param e evento
     */
    @FXML
    private void loadFormClientes(ActionEvent e) {
        try {
            Node node = Business.loadFXML("clientes/FormClientes.fxml");
            rootPane.getChildren().clear();
            rootPane.getChildren().add(node);
        }
        catch (IOException ex) {
        	logger.warn(ex.getMessage());
        }
    }
    
    /**
     * Carrega no AnchorPane o form de registros de clientes
     * @param e evento
     */
    @FXML
    private void loadFromProdutos(ActionEvent e) {
        try {
            Node node = Business.loadFXML("produtos/FormProdutos.fxml");
            rootPane.getChildren().clear();
            rootPane.getChildren().add(node);
        }
        catch (IOException ex) {
        	logger.warn(ex.getMessage());
        }
    }
}