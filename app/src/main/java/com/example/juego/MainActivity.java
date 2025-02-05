package com.example.juego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //Variables
    private EditText edNombreJugador;
    private Spinner spinnerDificultad;
    private Button btnStartGame;

    private String nombreJugador;
    private String dificultadSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazamos los elementos de la interfaz
        edNombreJugador = findViewById(R.id.edNombreJugador_ADC);
        spinnerDificultad = findViewById(R.id.spinnerDificultad_ADC);
        btnStartGame = findViewById(R.id.btnStartGame_ADC);

        // Configuramos el Spinner de dificultad
        configurarSpinnerDificultad();

        // Configuramos el botón para iniciar el juego
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarJuego();
            }
        });

    }

    private void configurarSpinnerDificultad() {
        // Lista de niveles de dificultad
        String[] nivelesDificultad = {"Fácil", "Normal", "Difícil"};

        // Configuramos el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nivelesDificultad);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDificultad.setAdapter(adapter);

        // Manejamos la selección del usuario
        spinnerDificultad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dificultadSeleccionada = nivelesDificultad[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dificultadSeleccionada = "Normal"; // Valor predeterminado
            }
        });
    }

    private void iniciarJuego() {
        // Obtenemos el nombre del jugador
        nombreJugador = edNombreJugador.getText().toString().trim();

        if (nombreJugador.isEmpty()) {
            // Mostramos un mensaje si el nombre está vacío
            Toast.makeText(this, "Por favor, introduce tu nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos un intent para iniciar la actividad del juego
        Intent intent = new Intent(MainActivity.this, GameActivity.class);

        // Pasamos el nombre del jugador y la dificultad seleccionada a la siguiente actividad
        intent.putExtra("NOMBRE_JUGADOR", nombreJugador);
        intent.putExtra("DIFICULTAD", dificultadSeleccionada);

        // Iniciamos la actividad del juego
        startActivity(intent);
    }

}