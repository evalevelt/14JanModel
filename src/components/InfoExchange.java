package components;

import Jama.Matrix;

/**
 * Created by eva on 14/02/2017.
 */
public class InfoExchange
{
    public Matrix repos;
    public Matrix newFunding;
    public double[] bankDefaults1;
    public double[] hedgefundDefaults;
    public double[] bankDefaults2;
    public Matrix loanTerminationsHF;
    public Matrix repayments;

    public InfoExchange(int N_BANKS, int N_HEDGEFUNDS){
        this.repos=new Matrix(N_BANKS, N_HEDGEFUNDS);
        this.newFunding=new Matrix(N_BANKS, N_HEDGEFUNDS);
        this.repayments=new Matrix(N_BANKS, N_HEDGEFUNDS);
        this.bankDefaults1=new double[N_BANKS];
        this.bankDefaults2=new double[N_BANKS];
        this.hedgefundDefaults=new double[N_HEDGEFUNDS];
        this.loanTerminationsHF=new Matrix(N_BANKS, N_HEDGEFUNDS);

    }

    public void setrepos(Matrix newrepos){
        this.repos=newrepos;

    }

    public void reset(int N_BANKS, int N_HEDGEFUNDS){
        this.newFunding=new Matrix(N_BANKS, N_HEDGEFUNDS);
        this.repayments=new Matrix(N_BANKS, N_HEDGEFUNDS);
        this.bankDefaults1=new double[N_BANKS];
        this.bankDefaults2=new double[N_BANKS];
        this.hedgefundDefaults=new double[N_HEDGEFUNDS];
        this.loanTerminationsHF=new Matrix(N_BANKS, N_HEDGEFUNDS);
    }
}
