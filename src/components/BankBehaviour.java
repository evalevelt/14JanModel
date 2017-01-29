package components;

/**
 * Created by eva on 24/01/2017.
 */
public class BankBehaviour {
    Bank bank;
    Market market;
   public double lambda_min = 0.03;
   public double lambda_T=0.05;
   public double kappa_min=1.0;
    public double kappa_T=1.2;
    double beta=0.8;




    public BankBehaviour(){
        this.bank=null;
    }

    public void printBank(){
        System.out.println(this.bank.name);
    }

    public void updateBalanceSheet(){
        this.bank.Balancesheet.phi++;
    }

    public void setNO(){this.bank.NO=this.beta*this.bank.Balancesheet.getLiability();}

    public double getNO(){return this.bank.NO;}


    public void checkSolvency(){
        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()>=this.bank.Balancesheet.getLiability()){
            this.bank.D=1;
        } else {this.bank.D=0;}
    }

    public double returnLeverage(){
        double lambda = (this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()-this.bank.Balancesheet.getLiability())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()+this.bank.Balancesheet.getCash());
        return lambda;
    }

    public void checkLeverage(){
        double lambda = (this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()-this.bank.Balancesheet.getLiability())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()+this.bank.Balancesheet.getCash());
        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getTotalRepo()>0
                && this.bank.D==1 && lambda<lambda_min){
            this.bank.B=1;
        } else {this.bank.B=0;}
    }

    public void deleverRule1(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.Balancesheet.getTotalRepo());

        if(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()>0){
        this.bank.y=((this.bank.Balancesheet.phi*market.S)/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*(Gamma-this.bank.x);
            this.bank.z=((this.bank.Balancesheet.getCash())/(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*(Gamma-this.bank.x);
        }
        else {this.bank.y=0;
        this.bank.z=0;}

    }

    public void deleverRule2(){
        double Gamma = amountToDelever();
        this.bank.x=Math.min(Gamma, this.bank.Balancesheet.getTotalRepo());
        this.bank.y=Math.min(Gamma-this.bank.x, this.bank.Balancesheet.phi*market.S);
        double Gamma_=(lambda_min*(this.bank.Balancesheet.getTotalRepo())+this.bank.Balancesheet.getLiability()+(lambda_min-1)*(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*this.bank.B/lambda_min;
        this.bank.z=Math.max(Math.min(Gamma_-this.bank.x-this.bank.y, this.bank.Balancesheet.getCash()),0);
    }

    public void checkLCR(){
       if(this.bank.Balancesheet.getCash()-this.bank.z>=kappa_min*this.bank.NO){
           this.bank.D_=1;}
           else{this.bank.D_=0;}

       }

    public void setMarket(Market market){
        this.market=market;
    }

    public double giveFundingUpdate(int i){
        if(i==1){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_1*(1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo()));}
        if(i==2){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_2*(1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo()));}
        if(i==3){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_3*(1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo()));}
        if(i==4){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_4*(1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo()));}
        if(i==5){return this.bank.D*this.bank.D_*this.bank.getBalancesheet().Omega_5*(1-(this.bank.x/this.bank.getBalancesheet().getTotalRepo()));}
        else{return 0;}

    }

    public void getDefaultInfo(int i, int Def){
        if(i==1){this.bank.Def_1=Def;}
        else  if(i==2){this.bank.Def_2=Def;}
        else if(i==3){this.bank.Def_3=Def;}
        else if(i==4){this.bank.Def_4=Def;}
        else if(i==5){this.bank.Def_5=Def;}
        else{System.out.println("there is no hedgefund w this number"+i);}

    }

    public double amountToDelever(){
        double Gamma=(lambda_T*(this.bank.Balancesheet.getTotalRepo())+this.bank.Balancesheet.getLiability()+(lambda_T-1)*(this.bank.Balancesheet.phi*market.S+this.bank.Balancesheet.getCash()))*this.bank.B/lambda_T;
        return Gamma;
    }


}
