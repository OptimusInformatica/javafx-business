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
package br.com.optimus.business.empresas;

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
 * Classe de controle do arquivo FXML FormEmpresas.fxml
 * <p>
 * Form responsável por apresentar os registros de empresas</p>
 * <p>
 * Alteração dos dados da empresa</p>
 * <p>
 * Inserir uma nova empresa</p>
 *
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 */
public class FormEmpresas implements Initializable {

    private final Connection dbh = Business.getConn();
    private final Logger logger = LogManager.getLogger(FormEmpresas.class);
    private final String usuario = "56064acf-8059-11e8-a7b4-001a3f6e17d6";
    private final Alert info = new Alert(AlertType.INFORMATION);
    private final Alert erro = new Alert(AlertType.ERROR);
    private final Alert confirm = new Alert(AlertType.CONFIRMATION);

    /**
     * TableView para visualizar os registros de empresas
     */
    @FXML
    private TableView<EmpresasModel> table;
    /**
     * Campo do formulário para o nome da empresa
     */
    @FXML
    private TextField empresa;
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
     * Pupula o TableView com os registros da view bsn_empresa
     * <p>
     */
    Runnable populateTable = new Runnable() {
        @Override
        public void run() {
            try {
                // Prepara a consula na view bsn_empresa
                PreparedStatement stmt = dbh.prepareStatement("select * from bsn_empresa");
                // Executa a consulta
                ResultSet rs = stmt.executeQuery();
                // Limpa a TableView
                table.getItems().clear();
                // Faz o loop pelos registros da view
                while (rs.next()) {
                    // Adiciona a row na TableView
                    EmpresasModel.setItem(table.getItems(), rs);
                }
                // Adiciona o evento ao selecionar uma row da TableView
                table.getSelectionModel().selectedItemProperty().addListener(new RowSelect());
            } catch (SQLException ex) {
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
        EmpresasModel.setColumns(table.getColumns());
        Platform.runLater(populateTable);
    }

    /**
     * Salva um registro alterado
     * <p>
     * Função do evento OnClick do botão save do formulário
     *
     * @param e javafx.event.ActionEvent
     */
    @FXML
    private void saveRegistro(ActionEvent e) {
        Platform.runLater(() -> {
            try {
                TableView.TableViewSelectionModel<EmpresasModel> sModel = table.getSelectionModel();
                EmpresasModel model = sModel.getSelectedItem();

                model.empresa.set(empresa.getText());
                model.nome.set(nome.getText());
                model.documento.set(documento.getText());
                model.ie.set(ie.getText());
                model.im.set(im.getText());

                model.phone.set(phone.getText());
                model.email.set(email.getText());

                model.cep.set(cep.getText());
                model.numero.set(Integer.parseInt(numero.getText()));
                model.complemento.set(complemento.getText());

                Empresas.updateFromModel(model, dbh);

                table.getItems().set(sModel.getSelectedIndex(), model);
            }
            catch (NumberFormatException | SQLException ex) {
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
     * Thread para atualizar a TableView
     */
    Runnable threadAddRegistro = new Runnable() {
        
        @Override
        public void run() {
            try {
        	Empresas empresas = new Empresas(dbh);
        	boolean rst = empresas.add(
        		table.getItems(),
        		usuario,
        		empresa.getText(),
        		nome.getText(),
        		documento.getText(),
        		ie.getText(),
        		im.getText(),
        		phone.getText(),
        		email.getText(),
        		cep.getText(),
        		numero.getText(),
        		complemento.getText());
        	if (rst) {
        	    info.setTitle("Registro realizado com sucesso!");
        	    info.setContentText(String.format("O registro da empresa %s foi realizado com sucesso!", empresa.getText()));
        	    info.showAndWait();
        	}
        	
            }
            catch (SQLException | EmpresasException ex) {
        	logger.warn(ex.getMessage());
            }
        }
    };
    
    /**
     * Deleta um registro de empresa
     * @param event
     */
    @FXML
    private void deleteRegistro(ActionEvent event) {
        EmpresasModel model = table.getSelectionModel().getSelectedItem();
        if (model == null) {
            erro.setTitle("Selecione um registro");
            erro.setHeaderText(String.format("Por favor, selecione uma empresa"));
            erro.setContentText("Você deve selecionar o registro a ser deletado");
            erro.showAndWait();
            return ;
        }
        
        confirm.setTitle("Deletar o registro?");
        confirm.setHeaderText(String.format("Deletar a empresa %s?", model.empresa.get()));
        confirm.setContentText("Tem certeza que você quer deletar esse item?");
        Optional<ButtonType> rst = confirm.showAndWait();
        if (rst.get().equals(ButtonType.OK)) {
            Platform.runLater(threadDeleteRegistro);
        }
        
    }
    
    Runnable threadDeleteRegistro = new Runnable() {
        
        @Override
        public void run() {
            TableView.TableViewSelectionModel<EmpresasModel> sModel = table.getSelectionModel();
            EmpresasModel model = sModel.getSelectedItem();
            
            info.setTitle("Registro deletado");
            info.setHeaderText(String.format("A empresa %s foi deletada", model.empresa.get()));
            info.setContentText("Registro deletado com sucesso!");
            
            Empresas empresas = new Empresas(dbh);
            try {
        	if (!empresas.delete(model.keyEmpresa.get())) {
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
                erro.setTitle("Erro SQL");
                erro.setHeaderText("Erro na consulta SQL");
                erro.setContentText(String.format("Erro [%s]", ex.getMessage()));
                erro.showAndWait();
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
    class RowSelect implements ChangeListener<EmpresasModel> {

        /**
         * Evento ao selecionar ou deselecionar uma row da TableView
         *
         * @param observable ObservableValue<? extends EmpresasModel>
         * @param oldModel objeto EmpresasModel
         * @param model objeto EmpresasModel
         */
        @Override
        public void changed(ObservableValue<? extends EmpresasModel> observable, EmpresasModel oldModel, EmpresasModel model) {
            // Verifica se tem selecionado uma row valida
            if (table.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            empresa.setText(model.empresa.get());
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
