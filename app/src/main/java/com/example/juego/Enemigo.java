package com.example.juego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Enemigo {

    private Bitmap bitmap; // Imagen de la nave enemiga
    private int x, y; // Posición de la nave enemiga en la pantalla
    private int speed; // Velocidad de la nave enemiga
    private boolean movingRight; // Estado de movimiento
    private int screenWidth; // Ancho de la pantalla para los límites


    // Constructor de la nave enemiga, recibe las coordenadas iniciales
    public Enemigo(Context context, int startX, int startY, int screenWidth) {
        // Cargar y escalar la imagen de la nave enemiga
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemigo);
        bitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true); // Escalar la imagen a 100x100 px

        //Establecemos las posiciones iniciales
        this.x = startX;
        this.y = startY;

        this.speed = 10; // Establecemos la velocidad de movimiento de la nave
        this.movingRight = true; // La nave se mueve hacia la derecha inicialmente
        this.screenWidth = screenWidth; // Guardamos el ancho de la pantalla para los límites

    }

    public void update(float speedFactor) {
        if (movingRight) {
            x += speed * speedFactor; // Ajustar velocidad con el factor
        }
    }

    // Método para dibujar la nave en la pantalla
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null); // Dibuja la imagen de la nave en la posición (x, y)
    }

    // Métodos para obtener las posiciones X e Y de la nave
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
