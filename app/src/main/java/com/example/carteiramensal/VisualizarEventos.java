package com.example.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ferramentas.EventosDB;
import modelo.Evento;

public class VisualizarEventos extends AppCompatActivity {



    private TextView tituloTxt;
    private ListView listaEventos;
    private TextView totalTxt;
    private Button novoBtn;
    private Button cancelarBtn;

    private ArrayList<Evento> eventos;
    private ItemListaEvento adapter;

    //operacao = 0 indica entrada e operacao igual a 1 indica saida
    private int operacao = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_eventos);

        tituloTxt = (TextView) findViewById(R.id.tituloTxt);
        listaEventos = (ListView) findViewById(R.id.listaEventos);
        totalTxt = (TextView) findViewById(R.id.valorTotalTxt);
        novoBtn = (Button) findViewById(R.id.novoBtn);
        cancelarBtn = (Button) findViewById(R.id.cancelarBtn);

        Intent intencao = getIntent();
        operacao = intencao.getIntExtra("acao", -1);
        //0-entrada e 1-saída

        ajustaOperacao();
        cadastrarEventos();

        carregaEventosLista();

    }



    private void cadastrarEventos(){
        novoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(operacao != -1){
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                    if(operacao == 0){
                        trocaAct.putExtra("acao", 0);
                        startActivityForResult(trocaAct, 0);
                    }else{
                        trocaAct.putExtra("acao", 1);
                        startActivityForResult(trocaAct, 1);
                    }


                }


            }
        });
        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ajustaOperacao(){
        //vamos precisar realizar uma busca no banco a respeito dos eventos a serem apresentados na lista

        if(operacao == 0){
            tituloTxt.setText("Entrada");
        }else{
            if(operacao == 1){
                tituloTxt.setText("Saída");
            }else{
                //erro na configuração da intent
                Toast.makeText(VisualizarEventos.this, "erro no parametro acao", Toast.LENGTH_LONG).show();

            }
        }
    }
    private void carregaEventosLista(){
        eventos = new ArrayList<>();

        //aqui ocorre a busca dos eventos no banco de dados

        EventosDB db = new EventosDB(VisualizarEventos.this);
        eventos = db.buscaEvento(operacao,MainActivity.dataAPP);

        adapter = new ItemListaEvento(getApplicationContext(),eventos);
        listaEventos.setAdapter(adapter);

        listaEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int indice, long id) {

                Evento eventoSelecionado = eventos.get(indice);

                Intent novoFluxo = new Intent(VisualizarEventos.this, CadastroEdicaoEvento.class);

                if (operacao == 0){
                    //editando um evento de entrada
                    novoFluxo.putExtra("acao", 2);
                }else {
                    //editando um evento de saida
                    novoFluxo.putExtra("acao", 3);
                }

                novoFluxo.putExtra("id", eventoSelecionado.getId()+"");

                startActivityForResult(novoFluxo, operacao);
            }
        });

        //agora somamos todos os valores para apresentar no total
        double total =  0.0;

        for (int i = 0; i < eventos.size(); i++) {
            total += eventos.get(i).getValor();
        }
            totalTxt.setText(String.format("%.2f", total) );

        }

        protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {
            super.onActivityResult(codigoRequest, codigoResultado, data);

            carregaEventosLista();
        }


    }



