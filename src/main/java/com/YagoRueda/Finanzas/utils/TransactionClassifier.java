package com.YagoRueda.Finanzas.utils;

import ai.onnxruntime.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class TransactionClassifier {

    public static String clasifyTransaction(String description) throws IllegalArgumentException, OrtException {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("No puedes tener una descripción vacía");
        }

        try {
            // 1️⃣ Cargar el modelo desde los recursos dentro del JAR
            InputStream modelStream = TransactionClassifier.class
                    .getClassLoader()
                    .getResourceAsStream("modelo_embebido.onnx");

            if (modelStream == null) {
                throw new FileNotFoundException("No se encontró modelo_embebido.onnx en resources");
            }

            // 2️⃣ Copiar a archivo temporal (ONNX Runtime requiere archivo físico)
            Path tempModel = Files.createTempFile("modelo_embebido", ".onnx");
            Files.copy(modelStream, tempModel, StandardCopyOption.REPLACE_EXISTING);

            // 3️⃣ Cargar el modelo desde el archivo temporal
            try (OrtEnvironment env = OrtEnvironment.getEnvironment();
                 OrtSession.SessionOptions options = new OrtSession.SessionOptions();
                 OrtSession session = env.createSession(tempModel.toString(), options)) {

                // Crear el tensor de entrada
                OnnxTensor inputTensor = OnnxTensor.createTensor(env, new String[][]{{description}});

                Map<String, OnnxTensor> inputs = Map.of(
                        session.getInputNames().iterator().next(),
                        inputTensor
                );

                // Ejecutar inferencia
                OrtSession.Result results = session.run(inputs);

                OnnxValue outVal = results.get(0);
                Object rawOutput = outVal.getValue();

                String categoria;

                if (rawOutput instanceof String[]) {
                    categoria = ((String[]) rawOutput)[0];
                } else if (rawOutput instanceof String[][]) {
                    categoria = ((String[][]) rawOutput)[0][0];
                } else {
                    throw new IllegalStateException("Salida del modelo desconocida: " + rawOutput.getClass());
                }

                results.close();
                inputTensor.close();

                return categoria;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error clasificando transacción: " + e.getMessage(), e);
        }
    }

}



