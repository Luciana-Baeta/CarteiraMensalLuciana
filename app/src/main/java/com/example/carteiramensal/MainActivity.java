package com.example.carteiramensal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ferramentas.EventosDB;
import modelo.Evento;

public class MainActivity extends AppCompatActivity {

    private TextView titulo;
    private TextView entrada;
    private TextView saida;
    private TextView saldo;
    private ImageButton entradaBtn;
    private ImageButton saidaBtn;
    private Button anteriorBtn;
    private Button proxBtn;
    private Button novoBtn;
    private Calendar hoje;
    static Calendar dataAPP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //criando o link entre os componentes JAVA x XML
        titulo = (TextView) findViewById(R.id.tituloMain);
        entrada = (TextView) findViewById(R.id.entradaTxt);
        saida = (TextView) findViewById(R.id.saidaTxt);
        saldo = (TextView) findViewById(R.id.saldoTxt);

        entradaBtn = (ImageButton) findViewById(R.id.entradaBtn);
        saidaBtn = (ImageButton) findViewById(R.id.saidaBtn);

        anteriorBtn = (Button) findViewById(R.id.anteriorBtn);
        proxBtn = (Button) findViewById(R.id.proximoBtn);
        novoBtn = (Button) findViewById(R.id.novoBtn);

        //responsavel por implementar todos os eventos de botoes
        cadastroEventos();

        //recupero e mostro a data e hora atual
        dataAPP = Calendar.getInstance();
        hoje = Calendar.getInstance();

        mostraDataApp();
        atualizaValores();
        configuraPermissoes();
    }

    private void configuraPermissoes() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }


    private void cadastroEventos(){
        anteriorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaiMesAnterior();
            }
        });

        proxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaMes(+1);
            }
        });
        novoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventosDB db = new EventosDB(MainActivity.this);
                //db.insereEvento();

                //Toast.makeText(MainActivity.this, db.getDatabaseName(), Toast.LENGTH_LONG).show();

            }
        });

        entradaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 0);

                startActivityForResult(trocaAct, 0);
            }
        });

        saidaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 1);

                //pedimos para iniciar a activity passada como parâmetro
                startActivityForResult(trocaAct, 1);
            }
        });


    }

    private void mostraDataApp(){
        //0 - janeiro, 1 - fevereiro, ..., 12 - dezembro
        String nomeMes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
                "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        int mes = dataAPP.get(Calendar.MONTH);
        int ano = dataAPP.get(Calendar.YEAR);

        titulo.setText(nomeMes[mes] +  "/" + ano);
    }

    private void vaiMesAnterior(){

        dataAPP.add(Calendar.MONTH, -1);

        //aqui temos quue realizar uma busca no banco de dados (avaliar se existem meses anteriores cadastrados)
        mostraDataApp();
    }

    private void atualizaMes(int ajuste){

        dataAPP.add(Calendar.MONTH, ajuste);

        //proximo mes (nao pode passar do mes atual)
        if(ajuste>0){
            if(dataAPP.after(hoje)){
                dataAPP.add(Calendar.MONTH, -1);
            }

        }
        else {
            //aqui temos quue realizar uma busca no banco de dados (avaliar se existem meses anteriores cadastrados)
        }

        mostraDataApp();
        atualizaValores();
    }

    private void atualizaValores() {

        //buscando entradas e saidas cadastradas ára esse mes no banco de dados
        EventosDB db = new EventosDB(MainActivity.this);

        ArrayList<Evento> saidas = db.buscaEvento(1, dataAPP);
        ArrayList<Evento> entradasLista = db.buscaEvento(0, dataAPP);


        //somando todos os valoes dos eventos recuperados em banco
        double entradaTotal = 0.0 ;
        double saidaTotal = 0.0;

        for (int i = 0; i < entradasLista.size(); i++){
            entradaTotal += entradasLista.get(i).getValor();
        }

        //mostrando os valores para o user
        double saldoTotal = entradaTotal - saidaTotal;

        entrada.setText(String.format("%.2f",entradaTotal) );
        saida.setText(String.format("%.2f",saidaTotal) );
        saldo.setText(String.format("%.2f",saldoTotal) );
    }

    protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {

        super.onActivityResult(codigoRequest, codigoResultado, data);
        atualizaValores();
    }
}