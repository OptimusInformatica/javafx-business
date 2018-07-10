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
package br.com.optimus.business.produtos;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.optimus.business.Business;
import br.com.optimus.fx.DoubleChangeListener;
import br.com.optimus.fx.IntChangeListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import org.controlsfx.control.textfield.TextFields;


/**
 * @author Brasilio Thomazo
 * @version 1.0.0.0
 * @date 7 de jul de 2018
 *
 */
public class FormProdutos implements Initializable {
    private final Connection dbh = Business.getConn();
    private final String usuario = "56064acf-8059-11e8-a7b4-001a3f6e17d6";
    private final Logger logger = LogManager.getLogger(FormProdutos.class);
    private final Alert info = new Alert(AlertType.INFORMATION);
    private final Alert erro = new Alert(AlertType.ERROR);
    private final Alert confirm = new Alert(AlertType.CONFIRMATION);
    
    @FXML private TableView<ProdutosModel> table;
    @FXML private TextField produto;
    @FXML private Spinner<Integer> estoque;
    @FXML private TextField barcode;
    @FXML private ComboBox<String> fabricantes;
    @FXML private ComboBox<String> tipos;
    @FXML private ComboBox<String> categorias;
    @FXML private Spinner<Double> custo;
    @FXML private Spinner<Double> venda;
    @FXML private Spinner<Double> peso;
    @FXML private Spinner<Double> altura;
    @FXML private Spinner<Double> largura;
    @FXML private Spinner<Double> profundidade;

    /**
     * Inicializa o FXML
     * @param location URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	venda.getEditor().textProperty().addListener(new DoubleChangeListener(venda));
	custo.getEditor().textProperty().addListener(new DoubleChangeListener(custo));
	estoque.getEditor().textProperty().addListener(new IntChangeListener(estoque));
	ProdutosModel.setColumns(table.getColumns());
	
	try {
	    Fabricantes.list(dbh, fabricantes.getItems());
	    Tipos.list(dbh, tipos.getItems());
	    Categorias.list(dbh, categorias.getItems());
	    Statement stmt = dbh.createStatement();
	    ResultSet rs = stmt.executeQuery("select * from bsn_produto order by cod_produto");
	    table.getItems().clear();
	    while (rs.next()) {
		ProdutosModel.setItem(table.getItems(), rs);
	    }
	}
	catch (SQLException ex) {
	    logger.warn(ex.getMessage());
	    alertSQLError(ex);
	}
	TextFields.bindAutoCompletion(fabricantes.getEditor(), fabricantes.getItems());
	TextFields.bindAutoCompletion(tipos.getEditor(), tipos.getItems());
	TextFields.bindAutoCompletion(categorias.getEditor(), categorias.getItems());
	table.getSelectionModel().selectedItemProperty().addListener(new RowSelect());
    }
    
    /**
     * Registra um novo produto
     * @param event ActionEvent
     */
    @FXML private void addProduto(ActionEvent event) {
	try {
	    ProdutosModel model = new ProdutosModel(dbh);
	    model.produto.set(produto.getText());
	    model.setKeyUsuario(usuario);
            model.estoque.set(estoque.getValue());
            model.barcode.set(barcode.getText());
            // Seta o fabricante e cria caso ele não exista
            model.setFabricante(fabricantes.getSelectionModel().getSelectedItem());
            // Seta o tipo e cria caso ele não exista
            model.setTipo(tipos.getSelectionModel().getSelectedItem());
            // Seta a categoria e cria caso ela não exista
            model.setCategoria(categorias.getSelectionModel().getSelectedItem());
            // Seta o valor de custo e de venda
            model.custo.set(custo.getValue());
            model.venda.set(venda.getValue());
            model.peso.set(peso.getValue());
            model.altura.set(altura.getValue());
            model.largura.set(largura.getValue());
            model.profundidade.set(profundidade.getValue());
	    if (Produtos.add(dbh, model)) {
		info.setTitle("Produto registrado");
		info.setHeaderText("O novo produto foi registrado.");
		info.setContentText(String.format("%s registrado com sucesso!", model.produto.get()));
		info.showAndWait();
		// Adiciona o novo produto na tableview
		table.getItems().add(model);
	    }
	}
	catch (SQLException | ProdutosException ex) {
	    logger.warn(ex.getMessage());
    	    alertFatalError(ex);
	}
    }

    /**
     * Deleta um produto registrado
     * @param event ActionEvent
     */
    @FXML private void deleteProduto(ActionEvent event) {
        TableView.TableViewSelectionModel<ProdutosModel> sModel = table.getSelectionModel();
        ProdutosModel model = sModel.getSelectedItem();
        if (model == null) {
            return ;
        }
        
        confirm.setTitle("Deletar o registro?");
        confirm.setHeaderText(String.format("Deletar o produto %s?", model.produto.get()));
        confirm.setContentText("Tem certeza que você quer deletar esse item?");
        Optional<ButtonType> rst = confirm.showAndWait();
        if (rst.get().equals(ButtonType.OK)) {
            
        }
    }

    /**
     * <p>Salva um registro alterado</p>
     * <p>Função do evento OnClick do botão save do formulário</p>
     * @param event ActionEvent
     */
    @FXML
    private void updateProduto(ActionEvent event) {
        TableView.TableViewSelectionModel<ProdutosModel> sModel = table.getSelectionModel();
        ProdutosModel model = sModel.getSelectedItem();
        if (model == null) {
            return ;
        }
        Platform.runLater(() -> {
            try {
                model.produto.set(produto.getText());
                model.estoque.set(estoque.getValue());
                model.barcode.set(barcode.getText());
                // Seta o fabricante e cria caso ele não exista
                model.setFabricante(dbh, fabricantes.getSelectionModel().getSelectedItem());
                // Seta o tipo e cria caso ele não exista
                model.setTipo(tipos.getSelectionModel().getSelectedItem());
                // Seta a categoria e cria caso ela não exista
                model.setCategoria(categorias.getSelectionModel().getSelectedItem());
                // Seta o valor de custo e de venda
                model.custo.set(custo.getValue());
                model.venda.set(venda.getValue());
                model.peso.set(peso.getValue());
                model.altura.set(altura.getValue());
                model.largura.set(largura.getValue());
                model.profundidade.set(profundidade.getValue());
                // Faz o update na tabela bsn_produtos
                Produtos.update(dbh, model);
                // Atualiza a TableView
                table.getItems().set(sModel.getSelectedIndex(), model);
                info.setTitle("Produto editado.");
                info.setHeaderText("Produto editado com sucesso!");
                info.setContentText("As informações foram salvas.");
                info.showAndWait();
            }
            catch (SQLException | ProdutosException ex) {
        	alertFatalError(ex);
                logger.warn(ex.getMessage());
            }
        });
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
     * Classe para verificar o item selecionado e popular os campos para a
     * alteração
     *
     * @author Brasilio Thomazp
     * @version 1.0.0.0
     */
    class RowSelect implements ChangeListener<ProdutosModel> {

        /**
         * Evento ao selecionar ou deselecionar uma row da TableView
         *
         * @param observable ObservableValue<? extends ProdutosModel>
         * @param oldModel objeto ProdutosModel
         * @param model objeto ProdutosModel
         */
        @Override
        public void changed(ObservableValue<? extends ProdutosModel> observable, ProdutosModel oldModel, ProdutosModel model) {
            // Verifica se tem selecionado uma row valida
            if (table.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            produto.setText(model.produto.get());
            estoque.getValueFactory().setValue(model.estoque.get());
            barcode.setText(model.barcode.get());
            fabricantes.getSelectionModel().select(model.fabricante.get());
            tipos.getSelectionModel().select(model.tipo.get());
            categorias.getSelectionModel().select(model.categoria.get());
            custo.getValueFactory().setValue(model.custo.get());
            venda.getValueFactory().setValue(model.venda.get());
            peso.getValueFactory().setValue(model.peso.get());
            altura.getValueFactory().setValue(model.altura.get());
            largura.getValueFactory().setValue(model.largura.get());
            profundidade.getValueFactory().setValue(model.profundidade.get());
        }

    }

}