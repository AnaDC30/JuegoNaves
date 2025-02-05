package com.example.juego;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    // Hilo del juego para ejecutar la lógica en segundo plano
    private Thread gameThread;
    // Bandera que indica si el juego está corriendo
    private boolean isPlaying;
    // Bandera que indica si el juego ha terminado
    private boolean gameOver;


    // Referencia a la nave del jugador
    private Nave nave;
    // Lista de naves enemigas
    private ArrayList<Enemigo> enemigo;
    // Lista de explosiones
    private ArrayList<Explosion> explosion;
    // Puntuación del jugador


    // Dimensiones de la pantalla
    private int screenWidth, screenHeight;
    // Objeto para dibujar en el canvas
    private Paint paint;
    // Objeto random para generar valores aleatorios
    private Random random;
    //Calcula el tiempo desde la ultima actualizacion
    private long lastUpdateTime = 0;

    // Variables de dificultad
    private String dificultad; // Dificultad del juego
    private String nombreJugador; // Nombre del jugador
    private long enemySpawnDelay = 2000; // Tiempo por defecto entre la aparición de enemigos
    private float enemySpeedFactor = 1.0f; // Factor de velocidad por defecto para los enemigos

    // Tiempo de la última generación de enemigos y el intervalo entre generaciones
    private long lastEnemySpawnTime;

    //Manejo de música
    private SoundPool soundPool;
    private int explosionSound;
    private MediaPlayer mediaPlayer;  // Para la música de fondo


    // Constructor de la clase GameView
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Configuramos el foco del teclado y la interacción con la vista
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        // Inicializamos las variables
        nave = new Nave(context);
        enemigo = new ArrayList<>();
        explosion = new ArrayList<>();
        paint = new Paint();
        random = new Random();

        // Obtienemos las dimensiones de la pantalla
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Inicializamos el tiempo de la última generación de enemigos
        lastEnemySpawnTime = System.currentTimeMillis();
        // Generamos enemigos iniciales
        spawnInitialEnemies();

        // Creamos AudioAttributes para configurar el SoundPool
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        // Inicializamos el SoundPool
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(1) // Número máximo de streams de sonido que puedes reproducir simultáneamente
                .build();

        // Cargamos el sonido de la explosión
        explosionSound = soundPool.load(context, R.raw.explosion, 1);

        // Inicializamos y configuramos el MediaPlayer para la música de fondo
        mediaPlayer = MediaPlayer.create(context, R.raw.marcianos);
        mediaPlayer.setLooping(true);  // Reproducir en bucle
        mediaPlayer.setVolume(0.5f, 0.5f);

        // Iniciamos la música
        mediaPlayer.start();
    }

    //Metodo run
    @Override
    public void run() {
        // Marca de tiempo inicial en nanosegundos
        long lastTime = System.nanoTime();
        // Aseguramos que el juego corra a 60 frames por segundo (FPS).
        long targetTime = 1000000000 / 60;

        // Bucle principal del hilo del juego
        while (isPlaying) {
            if (gameOver) break;

            // Calculamos el tiempo transcurrido desde el último frame
            long now = System.nanoTime();
            long elapsedTime = now - lastTime;
            lastTime = now;

            // Log para ver si el hilo está corriendo
            Log.d("GameThread", "Game is running");

            // Si el tiempo transcurrido supera o iguala el tiempo objetivo, actualizamos el estado del juego
            if (elapsedTime >= targetTime) {
                update();
                draw();
                control();
            }
        }
    }

    // Aseguramos de que la actualización de la pantalla se haga sin retrasos.
    public void postInvalidateDelayed(long delayMillis) {
        super.postInvalidateDelayed(delayMillis);
    }


    // Método que actualiza la lógica del juego
    private void update() {
        // Calculamos el tiempo transcurrido desde la última actualización
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;

        if (deltaTime >= 10) { // Limitamos el tiempo de actualización (aproximadamente 60 FPS)
            lastUpdateTime = currentTime; // Actualizamos el tiempo de la última actualización

            // Actualizamos la nave del jugador
            nave.update(screenHeight);


            // Verificamos las colisiones entre la nave del jugador y las naves enemigas
            for (Enemigo enemy : enemigo) {
                if (nave.checkCollision(enemy)) {
                    // Reproducimos el sonido de la explosión
                    playExplosionSound();

                    // Si la nave del jugador colisiona con un enemigo
                    explosion.add(new Explosion(nave.getX(), nave.getY(), BitmapFactory.decodeResource(getResources(), R.drawable.explosion)));
                    explosion.add(new Explosion(enemy.getX(), enemy.getY(), BitmapFactory.decodeResource(getResources(), R.drawable.explosion)));

                    // Terminamos el juego
                    gameOver = true;
                    break;
                }
            }

            // Actualizamos las naves enemigas
            for (Enemigo enemy : enemigo) {
                enemy.update(enemySpeedFactor);
            }

            // Verificamos si hay que generar nuevos enemigos
            if (System.currentTimeMillis() - lastEnemySpawnTime > enemySpawnDelay && enemigo.size() < 3) {
                spawnEnemy(); // Generamos una nueva nave enemiga
                lastEnemySpawnTime = System.currentTimeMillis(); // Actualizamos el tiempo de la última generación
            }

            // Actualizamos las explosiones en la pantalla
            for (Explosion explosion : explosion) {
                explosion.update();
            }

            // Eliminamos las explosiones que ya no son visibles
            explosion.removeIf(explosion -> !explosion.isAlive());

            // Eliminamos los enemigos fuera de la pantalla
            enemigo.removeIf(enemy -> enemy.getX() > screenWidth);
        }
    }


    // Método para dibujar los elementos en la pantalla
    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0xFF000000); // Fondo negro

                // Dibujar la nave del jugador
                nave.draw(canvas);

                // Dibujar las naves enemigas
                for (Enemigo enemy : enemigo) {
                    enemy.draw(canvas);
                }

                // Dibujar las explosiones
                for (Explosion explosion : explosion) {
                    explosion.draw(canvas);
                }

                // Si el juego ha terminado, mostrar el mensaje de "GAME OVER"
                if (gameOver) {
                    paint.setTextSize(100);
                    paint.setColor(0xFFFFFFFF); // Color blanco
                    canvas.drawText("GAME OVER", screenWidth / 4, screenHeight / 2, paint);
                }

                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    // Método para reproducir el sonido
    private void playExplosionSound() {
        soundPool.play(explosionSound, 1.0f, 1.0f, 0, 0, 1.0f);
    }


    public void startGame() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Métodos para configurar dificultad
    public void setEnemySpawnDelay(long delay) {
        this.enemySpawnDelay = delay;
    }

    public void setEnemySpeedFactor(float speedFactor) {
        this.enemySpeedFactor = speedFactor;
    }

    // Controlamos la velocidad del juego (aproximadamente 60 FPS)
    private void control() {
        try {
            Thread.sleep(17); // Pausa el hilo para lograr los 60 FPS
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

    // Generamos los enemigos iniciales
    private void spawnInitialEnemies(){
        for (int i = 0; i < 3; i++) {
            spawnEnemy();
        }
    }

    // Método para generar un enemigo aleatoriamente
    private void spawnEnemy() {
        int y = random.nextInt(screenWidth - 100); // Posición aleatoria en el eje Y
        int x = -random.nextInt(500); // Aparece fuera de la pantalla
        enemigo.add(new Enemigo(getContext(), x, y, screenWidth));
        Log.d("EnemySpawn", "Enemy spawned at x: " + x + ", y: " + y);
    }

    // Manejo de teclas al presionar
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!nave.isMovingUp()) { // Solo activa si no está en movimiento
                nave.setMovingUp(true);
                Log.d("KeyEvent", "UP pressed: Moving Up activated.");
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!nave.isMovingDown()) { // Solo activa si no está en movimiento
                nave.setMovingDown(true);
                Log.d("KeyEvent", "DOWN pressed: Moving Down activated.");
            }
        }
        return true;
    }

    // Manejo de teclas al soltar
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (nave.isMovingUp()) { // Solo desactiva si está en movimiento
                nave.setMovingUp(false);
                Log.d("KeyEvent", "UP released: Moving Up deactivated.");
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (nave.isMovingDown()) { // Solo desactiva si está en movimiento
                nave.setMovingDown(false);
                Log.d("KeyEvent", "DOWN released: Moving Down deactivated.");
            }
        }
        return true;
    }


    // Métodos para reanudar y pausar el juego
        public void resumeGame() {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pauseGame() {
            try {
                isPlaying = false;
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    //Selecion de la dificultad
    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
        // Configura la dificultad en función del valor recibido
        // Puedes modificar otros parámetros de dificultad aquí
    }

    //Seleccion del nombre del jugador
    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        // Configura el nombre del jugador si es necesario
    }

}
