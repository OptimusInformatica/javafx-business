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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal
 *
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 */
public class Business {
	private final static Properties configs = new Properties();
	private static Connection dbh;
	
	/**
	 * Recupera o ponteiro de conexão com o banco de dados
	 * @return Connection ponteiro de conexão com o db
	 */
	public static Connection getConn() {
		return dbh;
	}
	
	/**
	 * Recupera o ponteiro da configuração
	 * @return Properties ponteiro de configuração
	 */
	public static Properties getConfig() {
		return configs;
	}

    /**
     * Carrega um arquivo FXML para um node
     * @param filename arquivo FXML
     * @return Parent Node do arquivo FXML
     * @throws IOException Erro ao tentar ler o arquivo FXML
     */
    public static Parent loadFXML(String filename) throws IOException {
        Parent parent = FXMLLoader.load(Business.class.getResource(filename));
        return parent;
    }

    /**
     * Carrega um arquivo FXML para uma nova janela
     * 
     * @param filename local do arquivo FXML
     * @return Stage widow
     * @throws IOException erro ao ler o arquivo FXML
     */
    public static Stage loadFXMLWindow(String filename) throws IOException {
        Parent parent = loadFXML(filename);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    /**
     * Carrega a configuração do arquivo business.properties
     * 
     * @throws IOException erro ao tentar ler o arquivo de configuração
     */
    private static void loadConfig() throws IOException {
        InputStream is = new FileInputStream("config.properties");
        configs.load(is);
        is.close();
    }

    /**
     * Conecta ao banco de dados através das configurações do arquivo: business.properties
     * 
     * @throws IOException Erro ao tentar ler o arquivo de configuração
     * @throws SQLException Erro ao fazer a conexão com db
     */
    public static void dbConnect() throws IOException, SQLException {
        if (configs.isEmpty()) {
            loadConfig();
        }
        String dsn = String.format("jdbc:%s://%s:%s/%s", configs.get("dbtype"), configs.get("dbhost"), configs.get("dbport"), configs.get("dbname"));
        dbh = DriverManager.getConnection(dsn, configs.getProperty("dbuser"), configs.getProperty("dbpass"));
    }

}
