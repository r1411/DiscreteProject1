package me.r1411.discreteiz1;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File workingDir = new File(System.getProperty("user.dir"));
        File inputFile = new File(workingDir, "input.txt");
        File outputFile = new File(workingDir, "output.txt");

        byte[][] graphMatrix;

        try {
            graphMatrix = getMatrixFromFile(inputFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не удалось прочитать файл: " + inputFile.getAbsolutePath(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Задана некорректная матрица смежности вершин", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        byte[][] closure = getTransClosure(graphMatrix, graphMatrix, graphMatrix, 1);

        try {
            writeMatrixToFile(outputFile, closure);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не удалось записать файл: " + outputFile.getAbsolutePath(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "Транзитивное замыкание вычислено. Файл с результатом: " + outputFile.getAbsolutePath(), "Успешно", JOptionPane.INFORMATION_MESSAGE);
    }

    static byte[][] addMatrix(byte[][] a, byte[][] b) {
        byte[][] c = new byte[a.length][a.length];
        for(int i = 0; i < a.length; i++)
            for(int j = 0; j < a.length; j++)
                c[i][j] = (byte) (a[i][j] | b[i][j]);
        return c;
    }

    static byte[][] multiplyMatrix(byte[][] a, byte[][] b) {
        byte[][] c = new byte[a.length][b[0].length];
        for(int i = 0; i < a.length; i++) {
            for(int j = 0; j < b[0].length; j++) {
                for(int k = 0; k < a[0].length; k++) {
                    c[i][j] |= a[i][k] & b[k][j];
                }
            }
        }
        return  c;
    }

    static byte[][] getTransClosure(byte[][] e, byte[][] eN, byte[][] tN, int n) {
        if(n == e.length)
            return tN;

        eN = multiplyMatrix(e, eN);
        tN = addMatrix(tN, eN);

        return getTransClosure(e, eN, tN, n + 1);
    }

    static byte[][] getMatrixFromFile(File file) throws IOException, NumberFormatException, IndexOutOfBoundsException {
        List<String> lines = Files.readAllLines(file.toPath());
        int vertexCount = Integer.parseInt(lines.remove(0));
        byte[][] matrix = new byte[vertexCount][vertexCount];

        for(int i = 0; i < vertexCount; i++) {
            String[] row = lines.get(i).trim().split(" ");
            for(int j = 0; j < row.length; j++) {
                matrix[i][j] = Byte.parseByte(row[j]);
            }
        }

        return matrix;
    }

    static void writeMatrixToFile(File file, byte[][] matrix) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("Результат: ");
        for(byte[] row : matrix) {
            StringBuilder line = new StringBuilder();
            for(byte b : row) {
                line.append(b).append(" ");
            }
            lines.add(line.toString().trim());
        }
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
    }

}