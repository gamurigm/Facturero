package com.example.facturero;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private TextView totalTextView;
    private ArrayList<ProductRow> productRows;
    private LinearLayout mainLayout;
    private TextView ivaTextView;
    private TextView subtotalTextView;

    // Variable para llevar el número de factura
    private int numeroFactura = 1; // Número inicial de factura
    private TextView facturaNumeroTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar variables
        tableLayout = findViewById(R.id.tableLayout);
        subtotalTextView = findViewById(R.id.subtotal);
        ivaTextView = findViewById(R.id.iva);
        totalTextView = findViewById(R.id.total);
        facturaNumeroTextView = findViewById(R.id.facturaNumero); // Referencia al TextView del número de factura

        mainLayout = findViewById(R.id.mainLayout);
        productRows = new ArrayList<>();

        // Mostrar el número de factura en el TextView
        actualizarNumeroFactura();

        // Botón para añadir nueva fila
        Button btnAddRow = findViewById(R.id.btnAddRow);
        btnAddRow.setOnClickListener(v -> addNewRow());

        // Botón para calcular
        Button btnCalcular = findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(v -> calculateTotal());

        // Botón para generar imagen
        Button btnGenerarImagen = findViewById(R.id.btnGenerarImagen);
        btnGenerarImagen.setOnClickListener(v -> generateInvoiceImage());

        // Añadir primera fila
        addNewRow();
    }

    private void addNewRow() {
        // Crear nueva fila
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Crear EditTexts para la nueva fila
        EditText descripcion = new EditText(this);
        EditText cantidad = new EditText(this);
        EditText precio = new EditText(this);

        // Configurar layouts
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

        descripcion.setLayoutParams(params);
        cantidad.setLayoutParams(params);
        precio.setLayoutParams(params);

        // Configurar hints y tipos de input
        descripcion.setHint("Producto");
        cantidad.setHint("0");
        precio.setHint("0.0");
        cantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        precio.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Crear ImageButton para eliminar la fila
        ImageButton btnEliminar = new ImageButton(this);
        btnEliminar.setImageResource(android.R.drawable.ic_delete); // Ícono de eliminar
        btnEliminar.setBackgroundColor(Color.TRANSPARENT); // Fondo transparente
        btnEliminar.setPadding(8, 8, 8, 8); // Agregar relleno
        btnEliminar.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Configurar acción para eliminar la fila
        btnEliminar.setOnClickListener(v -> {
            // Eliminar fila de la tabla y lista
            tableLayout.removeView(row);
            productRows.removeIf(productRow -> productRow.descripcion == descripcion);

            // Recalcular totales
            calculateTotal();
        });

        // Añadir componentes a la fila
        row.addView(descripcion);
        row.addView(cantidad);
        row.addView(precio);
        row.addView(btnEliminar); // Agregar ImageButton para eliminar

        // Añadir fila a la tabla
        tableLayout.addView(row);

        // Guardar referencia a la fila
        productRows.add(new ProductRow(descripcion, cantidad, precio));
    }

    private void calculateTotal() {
        double subtotal = 0;

        // Calcular subtotal
        for (ProductRow row : productRows) {
            try {
                int cantidad = Integer.parseInt(row.cantidad.getText().toString());
                double precio = Double.parseDouble(row.precio.getText().toString());
                subtotal += cantidad * precio;
            } catch (NumberFormatException e) {
                // Ignorar filas con datos inválidos
            }
        }

        // Calcular IVA (12%)
        double iva = subtotal * 0.12;
        // Calcular total
        double total = subtotal + iva;

        // Actualizar los TextViews con formato de dos decimales
        subtotalTextView.setText(String.format(Locale.getDefault(), "%.2f", subtotal));
        ivaTextView.setText(String.format(Locale.getDefault(), "%.2f", iva));
        totalTextView.setText(String.format(Locale.getDefault(), "%.2f", total));
    }

    // Clase auxiliar para mantener referencias a los campos de cada fila
    private static class ProductRow {
        EditText descripcion;
        EditText cantidad;
        EditText precio;

        ProductRow(EditText descripcion, EditText cantidad, EditText precio) {
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.precio = precio;
        }
    }

    private void generateInvoiceImage() {
        try {
            // Ocultar los botones temporalmente
            View buttonLayout = findViewById(R.id.buttonLayout);
            buttonLayout.setVisibility(View.GONE);

            // Crear Bitmap del layout
            View content = findViewById(R.id.mainLayout);
            content.setDrawingCacheEnabled(true);
            content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            content.buildDrawingCache();

            // Capturar el Bitmap
            Bitmap bitmap = Bitmap.createBitmap(content.getDrawingCache());
            content.setDrawingCacheEnabled(false);

            // Restaurar los botones
            buttonLayout.setVisibility(View.VISIBLE);

            // Crear nombre de archivo único
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            String fileName = "Factura_" + timeStamp + ".jpg";

            // Guardar imagen
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File file = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            // Incrementar el número de factura
            numeroFactura++;

            // Actualizar el número de factura en el TextView
            actualizarNumeroFactura();

            Toast.makeText(this, "Factura guardada en Imágenes: " + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar la factura: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Método para actualizar el número de factura en el TextView
    private void actualizarNumeroFactura() {
        String numeroFacturaTexto = "Factura #" + numeroFactura;
        facturaNumeroTextView.setText(numeroFacturaTexto);
    }
}
