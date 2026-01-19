package com.taskflow.util;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de informes JasperReports para TaskFlow
 * Genera informes PDF y HTML desde archivos .jasper compilados
 */
public class ReportManager {

    private static ReportManager instance;
    private final DatabaseManager dbManager;

    private ReportManager() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static ReportManager getInstance() {
        if (instance == null) {
            instance = new ReportManager();
        }
        return instance;
    }

    /**
     * Genera y muestra un informe
     *
     * @param reportPath       Ruta del archivo .jasper (ej: "/reports/informe_tareas_grafica.jasper")
     * @param parameters       Parámetros del informe (puede ser null si no hay parámetros)
     * @param embedded         true para mostrar incrustado en WebView, false para ventana nueva
     * @param embeddedWebView  WebView donde mostrar si es incrustado (null si no es incrustado)
     */
    public void generateAndShowReport(String reportPath, Map<String, Object> parameters,
                                     boolean embedded, WebView embeddedWebView) {
        try {
            // Crear carpeta de informes si no existe
            File informesDir = new File("informes");
            if (!informesDir.exists()) {
                informesDir.mkdir();
            }

            // Cargar el informe compilado (.jasper)
            JasperReport report = (JasperReport) JRLoader.loadObject(
                getClass().getResourceAsStream(reportPath)
            );

            // Obtener conexión a la base de datos
            Connection conexion = dbManager.getConexion();
            if (conexion == null) {
                showError("Error de conexión", "No se pudo conectar a la base de datos");
                return;
            }

            // Parámetros por defecto si no se proporcionan
            if (parameters == null) {
                parameters = new HashMap<>();
            }

            // Llenar el informe con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conexion);

            // Verificar que el informe tiene páginas
            if (jasperPrint.getPages().isEmpty()) {
                showInfo("Informe vacío", "El informe no generó páginas. Verifica los filtros aplicados.");
                return;
            }

            // Extraer nombre del archivo
            String reportName = reportPath.substring(reportPath.lastIndexOf('/') + 1, reportPath.lastIndexOf('.'));

            // Exportar a PDF
            String pdfPath = "informes/" + reportName + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
            System.out.println("✓ PDF generado: " + pdfPath);

            // Exportar a HTML
            String htmlPath = "informes/" + reportName + ".html";
            JasperExportManager.exportReportToHtmlFile(jasperPrint, htmlPath);
            System.out.println("✓ HTML generado: " + htmlPath);

            // Mostrar el informe
            if (embedded && embeddedWebView != null) {
                // Mostrar incrustado en el WebView proporcionado
                embeddedWebView.getEngine().load(new File(htmlPath).toURI().toString());
            } else {
                // Mostrar en ventana nueva
                showReportInNewWindow(htmlPath, reportName);
            }

        } catch (JRException e) {
            System.err.println("Error generando informe: " + e.getMessage());
            e.printStackTrace();
            showError("Error generando informe", e.getMessage());
        }
    }

    /**
     * Muestra el informe en una ventana nueva
     */
    private void showReportInNewWindow(String htmlPath, String reportName) {
        WebView webView = new WebView();
        webView.getEngine().load(new File(htmlPath).toURI().toString());

        StackPane stackPane = new StackPane(webView);
        Scene scene = new Scene(stackPane, 1000, 700);

        Stage stage = new Stage();
        stage.setTitle("TaskFlow - " + reportName.replace("_", " ").toUpperCase());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Genera informe de tareas con gráfica (INCRUSTADO)
     */
    public void generarInformeTareasGrafica(WebView webView) {
        generateAndShowReport(
            "/reports/informe_tareas_grafica.jasper",
            null,
            true,  // incrustado
            webView
        );
    }

    /**
     * Genera informe de asignaciones filtrado (NO INCRUSTADO)
     *
     * @param estadoFiltro Estado para filtrar ("abierta", "en_progreso", "completada", "retrasada", o null para todos)
     */
    public void generarInformeAsignacionesFiltrado(String estadoFiltro) {
        Map<String, Object> params = new HashMap<>();
        params.put("estado_filtro", estadoFiltro);

        generateAndShowReport(
            "/reports/informe_asignaciones_filtrado.jasper",
            params,
            false,  // NO incrustado (ventana nueva)
            null
        );
    }

    // Métodos auxiliares para mostrar mensajes
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
