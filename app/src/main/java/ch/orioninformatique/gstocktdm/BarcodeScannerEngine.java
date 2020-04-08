package ch.orioninformatique.gstocktdm;

import android.content.Context;
import android.os.AsyncTask;

import com.symbol.emdk.EMDKBase;
import com.symbol.emdk.EMDKException;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;

/**
 * Copyright (c) 2016 Sistrategia. http://www.sistrategia.com
 *
 * Based on code provided by Zebra Technologies at:
 * https://github.com/developer-zebra/samples-emdkforandroid-4_0/blob/BarcodeSample1/app/src/main/java/com/symbol/barcodesample1/MainActivity.java
 *
 * Created by:
 *    Alex     16/04/2016.
 */
public class BarcodeScannerEngine
        implements
        EMDKManager.EMDKListener,
        EMDKManager.StatusListener,
        Scanner.DataListener,
        Scanner.StatusListener
{
    public interface DataListener
    {
        void onData(String data);
    }

    public interface  StatusListener
    {
        void onStatus(String status);
    }

    private EMDKManager mEMDKManager = null;
    private BarcodeManager mBarcodeManager = null;
    private Scanner mScanner = null;

    private ArrayList<DataListener> mDataListeners = new ArrayList<DataListener>();
    private ArrayList<StatusListener> mStatusListeners = new ArrayList<StatusListener>();

    public BarcodeScannerEngine(final Context context) {
        if (context == null)
            throw new IllegalArgumentException("context");

        EMDKResults results = EMDKManager.getEMDKManager(context.getApplicationContext(),
                BarcodeScannerEngine.this);

        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS)
            throw new RuntimeException("Barcode Scanner Libraries not found");
    }

    public void addDataListener(DataListener listener) {
        if (listener != null)
            this.mDataListeners.add(listener);
    }

    public void removeDataListener(DataListener listener) {
        if (listener != null)
            this.mDataListeners.remove(listener);
    }

    public void addStatusListener(StatusListener listener) {
        if (listener != null)
            this.mStatusListeners.add(listener);
    }

    public void removeStatusListener(StatusListener listener) {
        if (listener != null)
            this.mStatusListeners.remove(listener);
    }

    public void resume() {
        if (this.mEMDKManager != null) {
            if (true) {
                this.mBarcodeManager =
                        (BarcodeManager) this.mEMDKManager.getInstance(
                                EMDKManager.FEATURE_TYPE.BARCODE);

                // Initialize scanner
                this.initScanner();
                this.setDecoders();
            } else {
                try {
                    this.mEMDKManager.getInstanceAsync(EMDKManager.FEATURE_TYPE.BARCODE, this);
                } catch (EMDKException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pause() {
        // De-initialize scanner
        this.deInitScanner();

        // Remove connection listener
        if (this.mBarcodeManager != null) {
            this.mBarcodeManager = null;
        }

        // Release the barcode manager resources
        if (this.mEMDKManager != null) {
            this.mEMDKManager.release(EMDKManager.FEATURE_TYPE.BARCODE);
        }
    }

    public void release() {
        if (this.mEMDKManager != null) {
            // Remove connection listener
            if (this.mBarcodeManager != null){
                this.mBarcodeManager = null;
            }

            // Release all the resources
            this.mEMDKManager.release();
            this.mEMDKManager = null;
        }

        this.mDataListeners.clear();
        this.mStatusListeners.clear();
    }

    private void initScanner() {
        if (this.mScanner == null) {
            this.mScanner = this.mBarcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);

            if (this.mScanner != null) {

                this.mScanner.addDataListener(this);
                this.mScanner.addStatusListener(this);

                /**
                 * Hard trigger. When this mode is set, the user has to manually
                 * press the trigger on the device after issuing the read call.
                 */
                this.mScanner.triggerType = Scanner.TriggerType.HARD;

                try {
                    this.mScanner.enable();

                    /**
                     * Starts an asynchronous Scan. The method will not turn ON the
                     * scanner. It will, however, put the scanner in a state in which
                     * the scanner can be turned ON either by pressing a hardware
                     * trigger or can be turned ON automatically.
                     */
                    this.startScan();
                } catch (ScannerException e) {
                    e.printStackTrace();
                }
            }else{
                this.onStatus("Status: " + "Failed to initialize the scanner device.");
            }
        }
    }

    private void startScan() {
        if(this.mScanner == null) {
            this.initScanner();
            return;
        }

        try {
            // Submit a new read.
            this.mScanner.read();
        } catch (ScannerException e) {
            this.onStatus("Status: " + e.getMessage());
        }
    }

    private void setDecoders() {
        if (this.mScanner == null) {
            this.initScanner();
        }

        if (this.mScanner != null) {
            try {
                ScannerConfig config = this.mScanner.getConfig();
                config.decoderParams.ean8.enabled = true;
                config.decoderParams.ean13.enabled = true;
                this.mScanner.setConfig(config);
            } catch (ScannerException e) {
                this.onStatus("Status: " + e.getMessage());
            }
        }
    }

    private void stopScan() {
        if (this.mScanner != null) {
            try {
                // Cancel the pending read.
                this.mScanner.cancelRead();
            } catch (ScannerException e) {
                this.onStatus("Status: " + e.getMessage());
            }
        }
    }

    private void deInitScanner() {
        if (this.mScanner != null) {
            try {
                this.mScanner.cancelRead();
                this.mScanner.disable();
            } catch (ScannerException e) {
                this.onStatus("Status: " + e.getMessage());
            }

            this.mScanner.removeDataListener(this);
            this.mScanner.removeStatusListener(this);

            try{
                this.mScanner.release();
            } catch (ScannerException e) {
                this.onStatus("Status: " + e.getMessage());
            }

            this.mScanner = null;
        }
    }

    private void onData(String data) {
        for (DataListener listener : this.mDataListeners)
            listener.onData(data);
    }

    private void onStatus(String status) {
        for (StatusListener listener : this.mStatusListeners)
            listener.onStatus(status);
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList <ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();

            for(ScanDataCollection.ScanData data : scanData) {
                String dataString =  data.getData();

                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        return params[0];
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        onData(result);
                    }
                }.execute(dataString);
            }
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.mEMDKManager = emdkManager;
        this.mBarcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        // Call this method to enable Scanner and its listeners
        this.initScanner();
    }

    @Override
    public void onStatus(EMDKManager.StatusData statusData, EMDKBase emdkBase) {
        if (statusData.getResult() == EMDKResults.STATUS_CODE.SUCCESS) {
            this.mBarcodeManager = (BarcodeManager) emdkBase;

            // Initialize scanner
            this.initScanner();
            this.setDecoders();
        }
    }

    @Override
    public void onClosed() {
        if (this.mEMDKManager != null) {
            // Remove connection listener
            if (this.mBarcodeManager != null){
                this.mBarcodeManager = null;
            }

            // Release all the resources
            this.mEMDKManager.release();
            this.mEMDKManager = null;
        }

        this.onStatus("Status: " + "EMDK closed unexpectedly! Please close and restart the application.");
    }

    @Override
    public void onStatus(StatusData statusData) {
        StatusData.ScannerStates state = statusData.getState();
        String statusString = "";
        AsyncTask<String, Void, String> statusUpdateTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                return params[0];
            }

            @Override
            protected void onPostExecute(String status) {
                onStatus(status);
            }
        };

        switch(state) {
            case IDLE:
                statusString = statusData.getFriendlyName()+" is enabled and idle...";
                statusUpdateTask.execute(statusString);

                try {
                    // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                    // may cause the scanner to pause momentarily before resuming the scanning.
                    // Hence add some delay (>= 100ms) before submitting the next read.
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    this.mScanner.read();
                } catch (ScannerException e) {
                    statusString = e.getMessage();
                    statusUpdateTask.execute(statusString);
                }
                break;
            case WAITING:
                statusString = "Scanner is waiting for trigger press...";
                statusUpdateTask.execute(statusString);
                break;
            case SCANNING:
                statusString = "Scanning...";
                statusUpdateTask.execute(statusString);
                break;
            case DISABLED:
                statusString = statusData.getFriendlyName()+" is disabled.";
                statusUpdateTask.execute(statusString);
                break;
            case ERROR:
                statusString = "An error has occurred.";
                statusUpdateTask.execute(statusString);
                break;
            default:
                break;
        }
    }
}
