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
package br.com.optimus.business.clientes;

import br.com.optimus.business.Business;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe de controle do arquivo FXML FormClientes.fxml
 * <p>
 * Form responsável por apresentar os registros de clientes</p>
 * <p>
 * Alteração dos dados da cliente</p>
 * <p>
 * Inserir uma nova cliente</p>
 *
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 */
public class FormClientes implements Initializable {

    private final Connection dbh = Business.getConn();
    private final Logger logger = LogManager.getLogger(FormClientes.class);
    private final String usuario = "56064acf-8059-11e8-a7b4-001a3f6e17d6";
    private final Alert info = new Alert(AlertType.INFORMATION);
    private final Alert erro = new Alert(AlertType.ERROR);
    private final Alert confirm = new Alert(AlertType.CONFIRMATION);

    /**
     * TableView para visualizar os registros de clientes
     */
    @FXML
    private TableView<ClientesModel> table;
    /**
     * Campo do formulário para o nome da cliente
     */
    @FXML
    private TextField cliente;
    /**
     * Campo do formulário para a Razão Social
     */
    @FXML
    private TextField nome;
    /**
     * Campo do formulário para o CNPJ
     */
    @FXML
    private TextField documento;
    /**
     * Campo do formulário para a inscrição estadual
     */
    @FXML
    private TextField ie;
    /**
     * Campo do formulário para a inscrição municipal
     */
    @FXML
    private TextField im;
    /**
     * Campo do formulário para o número de telefone
     */
    @FXML
    private TextField phone;
    /**
     * Campo do formulário para o e-mail
     */
    @FXML
    private TextField email;
    /**
     * Campo do formulário para o CEP
     */
    @FXML
    private TextField cep;
    /**
     * Campo do formulário para o número do prédio
     */
    @FXML
    private TextField numero;
    /**
     * Campo do formulário para o complemento ao endereço
     */
    @FXML
    private TextField complemento;
    /**
     * Campo do formulário para o nome da rua
     */
    @FXML
    private TextField rua;
    /**
     * Campo do formulário para o nome do bairro
     */
    @FXML
    private TextField bairro;
    /**
     * Campo do formulário para o nome da cidade
     */
    @FXML
    private TextField cidade;
    /**
     * Campo do formulário para a sigla do estado
     */
    @FXML
    private TextField uf;

    /**
     * Thread em segundo plano
     * <p>
     * Pupula o TableView com os registros da view bsn_cliente
     * <p>
     */
    Runnable populateTable = new Runnable() {
        @Override
        public void run() {
            try {
                // Prepara a consula na view bsn_cliente
                PreparedStatement stmt = dbh.prepareStatement("select * from bsn_cliente");
                // Executa a consulta
                ResultSet rs = stmt.executeQuery();
                // Limpa a TableView
                table.getItems().clear();
                // Faz o loop pelos registros da view
                while (rs.next()) {
                    // Adiciona a row na TableView
                    ClientesModel.setItem(table.getItems(), rs);
                }
                // Adiciona o evento ao selecionar uma row da TableView
                table.getSelectionModel().selectedItemProperty().addListener(new RowSelect());
            } catch (SQLException ex) {
        	alertSQLError(ex);
                logger.warn(ex.getMessage());
            }
        }
    };

    /**
     * Inicializa o controle da calsse
     * <p>
     * Função chamada após o carregamento do FXML
     *
     * @param url arquivo FXML
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClientesModel.setColumns(table.getColumns());
        Platform.runLater(populateTable);
    }

    /**
     * <p>Salva um registro alterado</p>
     * <p>Função do evento OnClick do botão save do formulário</p>
     *
     * @param event ActionEvent
     */
    @FXML
    private void saveRegistro(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                TableView.TableViewSelectionModel<ClientesModel> sModel = table.getSelectionModel();
                ClientesModel model = sModel.getSelectedItem();

                model.cliente.set(cliente.getText());
                model.nome.set(nome.getText());
                model.documento.set(documento.getText());
                model.ie.set(ie.getText());
                model.im.set(im.getText());

                model.phone.set(phone.getText());
                model.email.set(email.getText());

                model.cep.set(cep.getText());
                model.numero.set(Integer.parseInt(numero.getText()));
                model.complemento.set(complemento.getText());

                Clientes.updateFromModel(model, dbh);

                table.getItems().set(sModel.getSelectedIndex(), model);
            }
            catch (NumberFormatException | SQLException ex) {
        	alertFatalError(ex);
                logger.warn(ex.getMessage());
            }
        });
    }
    
    /**
     * Adiciona um registro ao banco de dados
     * @param event ActionEvent
     */
    @FXML
    private void addRegistro(ActionEvent event) {
	Platform.runLater(threadAddRegistro);
    }
    
    /**
     * Informa para o usuário um erro de SQL
     * @param ex SQLException
     */
    private void alertSQLError(SQLException ex) {
        erro.setTitle("Erro SQL");
        erro.setHeaderText("Erro na consulta SQL");
        erro.setContentText(String.format("[%s]", ex.getMessage()));
        erro.showAndWait();
    }
    
    /**
     * Informa para o usuário um erro de execução do código
     * @param ex Exception
     */
    private void alertFatalError(Exception ex) {
        erro.setTitle("Erro de execução");
        erro.setHeaderText("Houve um erro na execução");
        erro.setContentText(String.format("[%s]", ex.getMessage()));
        erro.showAndWait();
    }
    
    
    /**
     * Thread para atualizar a TableView
     */
    Runnable threadAddRegistro = new Runnable() {
        
        @Override
        public void run() {
            try {
        	Clientes clientes = new Clientes(dbh);
        	boolean rst = clientes.add(
        		table.getItems(),
        		usuario,
        		cliente.getText(),
        		nome.getText(),
        		documento.getText(),
        		ie.getText(),
        		im.getText(),
        		phone.getText(),
        		email.getText(),
        		cep.getText(),
        		numero.getText(),
        		complemento.getText(),
        		rua.getText(),
        		bairro.getText(),
        		cidade.getText(),
        		uf.getText());
        	if (rst) {
        	    info.setTitle("Registro realizado com sucesso!");
        	    info.setContentText(String.format("O registro da cliente %s foi realizado com sucesso!", cliente.getText()));
        	    info.showAndWait();
        	}
        	
            }
            catch (SQLException | ClientesException ex) {
        	alertFatalError(ex);
        	logger.warn(ex.getMessage());
            }
        }
    };
    
    /**
     * Deleta um registro de cliente
     * @param event evento click
     */
    @FXML
    private void deleteRegistro(ActionEvent event) {
        ClientesModel model = table.getSelectionModel().getSelectedItem();
        if (model == null) {
            erro.setTitle("Selecione um registro");
            erro.setHeaderText(String.format("Por favor, selecione uma cliente"));
            erro.setContentText("Você deve selecionar o registro a ser deletado");
            erro.showAndWait();
            return ;
        }
        
        confirm.setTitle("Deletar o registro?");
        confirm.setHeaderText(String.format("Deletar a cliente %s?", model.cliente.get()));
        confirm.setContentText("Tem certeza que você quer deletar esse item?");
        Optional<ButtonType> rst = confirm.showAndWait();
        if (rst.get().equals(ButtonType.OK)) {
            Platform.runLater(threadDeleteRegistro);
        }
        
    }
    
    Runnable threadDeleteRegistro = new Runnable() {
        
        @Override
        public void run() {
            TableView.TableViewSelectionModel<ClientesModel> sModel = table.getSelectionModel();
            ClientesModel model = sModel.getSelectedItem();
            
            info.setTitle("Registro deletado");
            info.setHeaderText(String.format("O cliente %s foi deletado", model.cliente.get()));
            info.setContentText("Registro deletado com sucesso!");
            
            Clientes clientes = new Clientes(dbh);
            try {
        	if (!clientes.delete(model.keyCliente.get())) {
                    erro.setTitle("Erro desconhecido");
                    erro.setHeaderText(String.format("Houve algum erro desconhecido"));
                    erro.setContentText("Erro desconhecido ao tentar deletar o registro");
                    erro.showAndWait();
                    return ;
        	}
                table.getItems().remove(sModel.getSelectedIndex());
                info.showAndWait();
            }
            catch (SQLException ex) {
        	alertSQLError(ex);
                logger.warn(ex.getMessage());
            }
        }
    };

    /**
     * Classe para verificar o item selecionado e popular os campos para a
     * alteração
     *
     * @author Brasilio Thomazp
     * @version 1.0.0.0
     */
    class RowSelect implements ChangeListener<ClientesModel> {

        /**
         * Evento ao selecionar ou deselecionar uma row da TableView
         *
         * @param observable ObservableValue<? extends ProdutosModel>
         * @param oldModel objeto ProdutosModel
         * @param model objeto ProdutosModel
         */
        @Override
        public void changed(ObservableValue<? extends ClientesModel> observable, ClientesModel oldModel, ClientesModel model) {
            // Verifica se tem selecionado uma row valida
            if (table.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            cliente.setText(model.cliente.get());
            nome.setText(model.nome.get());
            documento.setText(model.documento.get());
            ie.setText(model.ie.get());
            im.setText(model.im.get());
            phone.setText(model.phone.get());
            email.setText(model.email.get());
            cep.setText(model.cep.get());
            numero.setText(String.valueOf(model.numero.get()));
            complemento.setText(model.complemento.get());
            rua.setText(model.rua.get());
            bairro.setText(model.bairro.get());
            cidade.setText(model.cidade.get());
            uf.setText(model.uf.get());
        }

    }

}
