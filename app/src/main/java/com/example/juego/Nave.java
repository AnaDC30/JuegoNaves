package com.example.juego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Nave {
    // Atributos para la nave del jugador
    private Bitmap bitmap; // Imagen de la nave
    private int x, y; // Posición de la nave en la pantalla
    private boolean movingUp, movingDown; // Indicadores de movimiento


    // Constructor de la nave del jugador
    public Nave(Context context) {
        // Cargar y escalar la imagen de la nave
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nave_jugador);
        bitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true); // Escalar la imagen a un tamaño adecuado

        // Inicializamos la posición de la nave
        x = 900; // Posición X inicial
        y = 900; // Posición Y inicial

        // Inicializamos los movimientos
        movingUp = false;
        movingDown = false;
    }

    /// Método para actualizar la posición de la nave
    public void update(int screenHeight) {
        // Usamos una velocidad fija en píxeles por fotograma
        float speed = 100f;  // Ajustamos la velocidad según sea necesario

        if (movingUp) {
            y -= speed;
        }

        if (movingDown) {
            y += speed;
        }

        if (y < 0) {
            y = 0;
        }
        if (y > screenHeight - getHeight()) {
            y = screenHeight - getHeight();
        }
    }


    // Método para dibujar la nave en el canvas
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    // Método para comprobar si la nave ha colisionado con una nave enemiga
    public boolean checkCollision(Enemigo enemigo) {
        // Verifica si las coordenadas del enemigo están dentro del área de la nave
        return enemigo.getX() >= x && enemigo.getX() <= x + bitmap.getWidth()
                && enemigo.getY() >= y && enemigo.getY() <= y + bitmap.getHeight();
    }

    // Métodos para obtener la posición de la nave
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Métodos para configurar las direcciones de movimiento
    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    // Métodos para obtener el estado del movimiento
    public boolean isMovingUp() {
        return movingUp;
    }

    public boolean isMovingDown() {
        return movingDown;
    }

    // Método para obtener el tamaño de la nave
    public int getHeight() {
        return bitmap.getHeight(); // Devuelve el alto del bitmap
    }

}
