package com.example.facturero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText descriptionInput;
    private EditText quantityInput;
    private EditText priceInput;
    private Button addItemButton;
    private Button generateInvoiceButton;
    private RecyclerView itemsRecyclerView;
    private TextView totalTextView;
    private List<InvoiceItem> invoiceItems;
    private InvoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        descriptionInput = findViewById(R.id.descriptionInput);
        quantityInput = findViewById(R.id.quantityInput);
        priceInput = findViewById(R.id.priceInput);
        addItemButton = findViewById(R.id.addItemButton);
        generateInvoiceButton = findViewById(R.id.generateInvoiceButton);
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        totalTextView = findViewById(R.id.totalTextView);
    }

    private void setupRecyclerView() {
        invoiceItems = new ArrayList<>();
        adapter = new InvoiceAdapter(invoiceItems);
        itemsRecyclerView.setAdapter(adapter);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        addItemButton.setOnClickListener(v -> addInvoiceItem());
        generateInvoiceButton.setOnClickListener(v -> generateInvoicePDF());
    }

    private void addInvoiceItem() {
        try {
            String description = descriptionInput.getText().toString();
            if (description.isEmpty()) {
                descriptionInput.setError("Ingrese una descripción");
                return;
            }

            String quantityStr = quantityInput.getText().toString();
            if (quantityStr.isEmpty()) {
                quantityInput.setError("Ingrese una cantidad");
                return;
            }
            int quantity = Integer.parseInt(quantityStr);

            String priceStr = priceInput.getText().toString();
            if (priceStr.isEmpty()) {
                priceInput.setError("Ingrese un precio");
                return;
            }
            double price = Double.parseDouble(priceStr);

            InvoiceItem item = new InvoiceItem(description, quantity, price);
            invoiceItems.add(item);
            adapter.notifyItemInserted(invoiceItems.size() - 1);

            updateTotal();
            clearInputs();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (InvoiceItem item : invoiceItems) {
            total += item.getTotal();
        }
        totalTextView.setText(String.format("Total: $%.2f", total));
    }

    private void clearInputs() {
        descriptionInput.setText("");
        quantityInput.setText("");
        priceInput.setText("");
        descriptionInput.requestFocus();
    }

    private void generateInvoicePDF() {
        if (invoiceItems.isEmpty()) {
            Toast.makeText(this, "Agregue items a la factura primero", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show();
    }
}

class InvoiceItem {
    private String description;
    private int quantity;
    private double price;

    public InvoiceItem(String description, int quantity, double price) {
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public double getTotal() {
        return quantity * price;
    }

    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}

class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    private List<InvoiceItem> items;

    public InvoiceAdapter(List<InvoiceItem> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invoice_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InvoiceItem item = items.get(position);
        holder.descriptionText.setText(item.getDescription());
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        holder.priceText.setText(String.format("$%.2f", item.getPrice()));
        holder.totalText.setText(String.format("$%.2f", item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionText, quantityText, priceText, totalText;

        public ViewHolder(View view) {
            super(view);
            descriptionText = view.findViewById(R.id.itemDescription);
            quantityText = view.findViewById(R.id.itemQuantity);
            priceText = view.findViewById(R.id.itemPrice);
            totalText = view.findViewById(R.id.itemTotal);
        }
    }
}