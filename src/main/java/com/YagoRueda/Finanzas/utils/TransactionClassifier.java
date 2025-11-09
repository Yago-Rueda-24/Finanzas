package com.YagoRueda.Finanzas.utils;

import ai.onnxruntime.*;

import java.util.Map;

public class TransactionClassifier {

    public static String clasifyTransaction(String description) throws IllegalArgumentException, OrtException {
        try (OrtEnvironment env = OrtEnvironment.getEnvironment();
             OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
             OrtSession session = env.createSession("src/main/resources/modelo_embebido.onnx", opts)) {

            if (description == null || description.isEmpty()) {
                throw new IllegalArgumentException("No puedes tener una descripción vaica");
            }
            String categoria = null;

            // Crear tensor de entrada (el modelo espera [batch_size, 1])
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, new String[][]{{description}});

            // Ejecutar inferencia
            Map<String, OnnxTensor> inputs = Map.of(session.getInputNames().iterator().next(), inputTensor);
            OrtSession.Result results = session.run(inputs);


            OnnxValue outVal = results.get(0);
            Object rawOutput = outVal.getValue();

            // Cubre el caso del array simple
            if (rawOutput instanceof String[]) {
                String[] output = (String[]) rawOutput;
                categoria = output[0];


            // Cubre el caso de un array 2D
            } else if (rawOutput instanceof String[][]) {
                String[][] output = (String[][]) rawOutput;
                categoria = output[0][0];


            }

            results.close();
            inputTensor.close();

            return categoria;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}
