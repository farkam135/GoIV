package com.kamron.pogoiv;


import android.content.res.Resources;

public enum ScreenGeometryHelper {
    INSTANCE;

    public final CropParameters monTypeCrop;
    public final CropParameters monNameCrop;
    public final CropParameters monCPCrop;
    public final CropParameters monHPCrop;
    public final CropParameters monCandyNameCrop;
    public final CropParameters monCandyAmountCrop;
    public final CropParameters monEvolutionCostCrop;
    public final CropParameters mon3DModelCrop;
    public final CropParameters monIdentifierCrop;
    public final CropParameters monAppraisalCrop;

    ScreenGeometryHelper() {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        double screenRatio = (double) screenHeight / (double) screenWidth;

        if (screenRatio > 1.9 && screenRatio < 2.06) {
            // Higher ratio, 18.5:9, a.k.a. S8 patch
            // This method forcefully includes the bottom nav-bar of the S8 in the resolution and height calculations,
            // so that the s8 and s8+ can use the same percentages
            screenHeight = (int) (screenWidth * screenRatio);

            this.monTypeCrop = new CropParameters(screenWidth, screenHeight, 0.365278, 0.53, 0.308333, 0.03);
            this.monNameCrop = new CropParameters(screenWidth, screenHeight, 0.1, 0.38, 0.85, 0.055);
            this.monCPCrop = new CropParameters(screenWidth, screenHeight, 0.25, 0.05, 0.5, 0.046);
            this.monHPCrop = new CropParameters(screenWidth, screenHeight, 0.357, 0.45, 0.285, 0.025);
            this.monCandyNameCrop = new CropParameters(screenWidth, screenHeight, 0.5, 0.62, 0.47, 0.036);
            this.monCandyAmountCrop = new CropParameters(screenWidth, screenHeight, 0.59, 0.60, 0.20, 0.038);
            this.monEvolutionCostCrop = new CropParameters(screenWidth, screenHeight, 0.625, 0.74, 0.2, 0.07);

        } else {
            // Default ratio, 16:9
            this.monTypeCrop = new CropParameters(screenWidth, screenHeight, 0.365278, 0.621094, 0.308333, 0.035156);
            this.monNameCrop = new CropParameters(screenWidth, screenHeight, 0.1, 0.45, 0.85, 0.055);
            this.monCPCrop = new CropParameters(screenWidth, screenHeight, 0.25, 0.064, 0.5, 0.046);
            this.monHPCrop = new CropParameters(screenWidth, screenHeight, 0.357, 0.52, 0.285, 0.0293);
            this.monCandyNameCrop = new CropParameters(screenWidth, screenHeight, 0.5, 0.73, 0.47, 0.026);
            this.monCandyAmountCrop = new CropParameters(screenWidth, screenHeight, 0.60, 0.695, 0.20, 0.038);
            this.monEvolutionCostCrop = new CropParameters(screenWidth, screenHeight, 0.625, 0.88, 0.2, 0.03);
        }

        // Screen ratio independent crop areas
        this.mon3DModelCrop = new CropParameters(screenWidth, screenHeight, 0.33, 0.25, 0.33, 0.2);
        this.monIdentifierCrop = new CropParameters(screenWidth, screenHeight, 0.1, 0.583335, 0.8, 0.039583);
        this.monAppraisalCrop = new CropParameters(screenWidth, screenHeight, 0.05, 0.89, 0.90, 0.07);
    }

    public class CropParameters {
        private int xPx;
        private int yPx;
        private int widthPx;
        private int heightPx;

        public CropParameters(int screenWidthPx, int screenHeightPx,
                              double percentX, double percentY, double percentWidth, double percentHeight) {
            this.xPx = (int) Math.round(screenWidthPx * percentX);
            this.yPx = (int) Math.round(screenHeightPx * percentY);
            this.widthPx = (int) Math.round(screenWidthPx * percentWidth);
            this.heightPx = (int) Math.round(screenHeightPx * percentHeight);
        }

        public int getXPx() {
            return xPx;
        }

        public int getYPx() {
            return yPx;
        }

        public int getWidthPx() {
            return widthPx;
        }

        public int getHeightPx() {
            return heightPx;
        }
    }
}
