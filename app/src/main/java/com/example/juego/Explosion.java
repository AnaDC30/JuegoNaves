package com.example.juego;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Explosion {

    // Atributos para la explosión
    private Bitmap bitmap; // Imagen de la explosión
    private int x, y; // Posición de la explosión en la pantalla
    private int lifespan; // Duración de la explosión (en ciclos de actualización)

    // Constructor de la clase Explosion
    public Explosion(int x, int y, Bitmap image) {
        this.x = x; // Posición X de la explosión
        this.y = y; // Posición Y de la explosión
        this.bitmap = Bitmap.createScaledBitmap(image, 100, 100, true); // Escalamos la imagen de la explosión a 100x100
        this.lifespan = 10; // Inicializamos la duración de la explosión (10 ciclos de actualización)
    }

    // Método para actualizar el estado de la explosión
    public void update() {
        lifespan--; // Reducimos la duración de la explosión
    }

    // Método para comprobar si la explosión está viva
    public boolean isAlive() {
        return lifespan > 0; // La explosión está viva mientras su duración sea mayor que 0
    }

    // Método para dibujar la explosión en el canvas
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null); // Dibujamos la imagen de la explosión en la posición
    }
}

