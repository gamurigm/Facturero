package com.example.facturero;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencia a los campos
        EditText cantidad1 = findViewById(R.id.cantidad1);
        EditText precio1 = findViewById(R.id.precio1);
        TextView total = findViewById(R.id.total);
        Button btnCalcular = findViewById(R.id.btnCalcular);

        // Botón de cálculo
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int cantidad = Integer.parseInt(cantidad1.getText().toString());
                    double precio = Double.parseDouble(precio1.getText().toString());
                    double totalCalculado = cantidad * precio;
                    total.setText(String.valueOf(totalCalculado));
                } catch (NumberFormatException e) {
                    total.setText("Error");
                }
            }
        });
    }
}
