package com.espertech.esper.view.stat;

/**
 * Bean for calculating the correlation (same to Microsoft Excel CORREL function).
 */
public final class CorrelationBean extends BaseStatisticsBean 
{
    /**
     * Return the correlation value for the two data series (Microsoft Excel function CORREL).
     * @return correlation value
     */
    public final double getCorrelation()
    {
        if (this.getN() == 0)
        {
            return Double.NaN;
        }

        double dx = this.getSumXSq() - (this.getXSum() * this.getXSum()) / this.getN();
        double dy = this.getSumYSq() - (this.getYSum() * this.getYSum()) / this.getN();

        if (dx == 0 || dy == 0)
        {
            return Double.NaN;
        }

        double sp = this.getSumXY() - this.getXSum() * this.getYSum() / this.getN();
        return sp / Math.sqrt(dx * dy);
    }
}
