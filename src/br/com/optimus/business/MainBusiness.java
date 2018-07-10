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
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal
 * 
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 */
public class MainBusiness extends Application {
    private static final Logger logger = LogManager.getLogger(Business.class);
    
    /**
     * Inicia a aplicação
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
	launch(args);
    }

    /**
     * Carrega o FXML principal
     *
     * @param stage Stage
     * @throws Exception Qualquer erro
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Business.dbConnect();
        }
        catch (SQLException | IOException ex) {
            logger.warn(ex.getMessage());
        }

        Parent root = Business.loadFXML("BusinessMain.fxml");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
