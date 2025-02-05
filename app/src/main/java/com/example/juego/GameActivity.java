package com.example.juego;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView; // Vista personalizada para el juego
    private String nombreJugador; // Nombre del jugador
    private String dificultad; // Nivel de dificultad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enlazamos el diseño XML
        setContentView(R.layout.activity_game);

        // Obtenemos los datos pasados desde la MainActivity
        nombreJugador = getIntent().getStringExtra("NOMBRE_JUGADOR");
        dificultad = getIntent().getStringExtra("DIFICULTAD");

        // Obtenemos referencia al GameView en el diseño
        gameView = findViewById(R.id.game_view);
        gameView.startGame(); // Iniciamos el juego al cargar la actividad

        if (gameView != null) {
            // Configuramos el GameView con los datos recibidos
            gameView.setDificultad(dificultad);
            gameView.setNombreJugador(nombreJugador);
        } else {
            Log.e("GameActivity", "GameView no se pudo inicializar.");
        }

        // Configuramos la dificultad del juego
        configurarDificultad();
    }

    private void configurarDificultad() {
        // Aseguramos que gameView no sea nulo antes de usarlo
        if (gameView != null && dificultad != null) {
            switch (dificultad) {
                case "Fácil":
                    gameView.setEnemySpawnDelay(3000); // Enemigos cada 3 segundos
                    gameView.setEnemySpeedFactor(0.5f); // Enemigos más lentos
                    break;
                case "Normal":
                    gameView.setEnemySpawnDelay(2000); // Enemigos cada 2 segundos
                    gameView.setEnemySpeedFactor(1.0f); // Velocidad normal
                    break;
                case "Difícil":
                    gameView.setEnemySpawnDelay(1000); // Enemigos cada 1 segundo
                    gameView.setEnemySpeedFactor(1.5f); // Enemigos más rápidos
                    break;
                default:
                    gameView.setEnemySpawnDelay(2000); // Valor por defecto
                    gameView.setEnemySpeedFactor(1.0f);
                    break;
            }
        } else {
            Log.e("GameActivity", "No se pudo configurar la dificultad.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pauseGame(); // Pausa el juego cuando la actividad se pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resumeGame(); // Reanuda el juego cuando la actividad se reanuda
    }
}
