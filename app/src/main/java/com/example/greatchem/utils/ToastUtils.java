package com.example.greatchem.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.greatchem.R;

public class ToastUtils {

    // Enum untuk mendefinisikan tipe Toast
    public enum CustomToastType {
        SUCCESS, WARNING, ERROR, INFO
    }

    /**
     * Menampilkan Toast kustom dengan ikon, judul opsional, pesan, dan aksen warna dinamis.
     * @param context Konteks aplikasi/aktivitas.
     * @param title Judul opsional Toast (bisa null atau string kosong).
     * @param message Pesan utama Toast.
     * @param type Tipe Toast (SUCCESS, WARNING, ERROR, INFO) untuk menentukan ikon dan warna aksen.
     * @param duration Durasi Toast (Toast.LENGTH_SHORT atau Toast.LENGTH_LONG).
     */
    public static void showCustomToast(@NonNull Context context,
                                       @Nullable String title,
                                       @NonNull String message,
                                       @NonNull CustomToastType type,
                                       int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        // Dapatkan referensi View
        View accentBar = layout.findViewById(R.id.toast_accent_bar);
        ImageView icon = layout.findViewById(R.id.toast_icon);
        TextView tvTitle = layout.findViewById(R.id.toast_title);
        TextView tvMessage = layout.findViewById(R.id.toast_message);

        // Set pesan
        tvMessage.setText(message);

        // Set judul (opsional)
        if (title != null && !title.isEmpty()) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        // Tentukan ikon dan warna aksen berdasarkan tipe Toast
        @DrawableRes int iconResId;
        int accentColor;

        switch (type) {
            case SUCCESS:
                iconResId = R.drawable.ic_check_circle; // Ganti dengan ikon sukses Anda
                accentColor = ContextCompat.getColor(context, R.color.toast_accent_success);
                break;
            case WARNING:
                iconResId = R.drawable.ic_warning_outline; // Ganti dengan ikon peringatan Anda
                accentColor = ContextCompat.getColor(context, R.color.toast_accent_warning);
                break;
            case ERROR:
                iconResId = R.drawable.ic_error_outline; // Ganti dengan ikon error Anda
                accentColor = ContextCompat.getColor(context, R.color.toast_accent_error);
                break;
            case INFO:
            default:
                iconResId = R.drawable.ic_info_outline; // Ganti dengan ikon info Anda
                accentColor = ContextCompat.getColor(context, R.color.toast_accent_info); // Atau warna netral/default
                break;
        }

        icon.setImageResource(iconResId);
        accentBar.setBackgroundColor(accentColor);

        // Buat dan tampilkan Toast
        Toast toast = new Toast(context);
        toast.setDuration(duration); // Toast.LENGTH_SHORT atau Toast.LENGTH_LONG
        toast.setView(layout);
        // Toast akan rata kiri secara otomatis karena struktur layout dan gravity default.
        // Jika Anda ingin mengubah posisi di layar (misal: di bagian bawah kiri):
        // toast.setGravity(Gravity.BOTTOM | Gravity.START, 0, 100);
        toast.show();
    }

    // Metode overload untuk panggilan yang lebih sederhana (tanpa judul, jika diinginkan)
    public static void showSuccessToast(@NonNull Context context, @NonNull String message, int duration) {
        showCustomToast(context, null, message, CustomToastType.SUCCESS, duration);
    }

    public static void showWarningToast(@NonNull Context context, @NonNull String message, int duration) {
        showCustomToast(context, null, message, CustomToastType.WARNING, duration);
    }

    public static void showErrorToast(@NonNull Context context, @NonNull String message, int duration) {
        showCustomToast(context, null, message, CustomToastType.ERROR, duration);
    }

    public static void showInfoToast(@NonNull Context context, @NonNull String message, int duration) {
        showCustomToast(context, null, message, CustomToastType.INFO, duration);
    }
}
