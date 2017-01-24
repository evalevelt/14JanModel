package components;

/**
 * Created by eva on 24/01/2017.
 */
public class Hedgefund {
    String name;
    Boolean D;
    HedgefundBalanceSheet Balancesheet;
    HedgefundBehaviour Behaviour;

    public Hedgefund(String name){
        this.name=name;
        this.D=true;
        this.Behaviour=new HedgefundBehaviour();
        this.Behaviour.hedgefund=this;
    }

    public void setBalancesheet(HedgefundBalanceSheet Balancesheet){
        this.Balancesheet=Balancesheet;
        Balancesheet.hedgefund=this;
    }

    public HedgefundBalanceSheet getBalancesheet(){
        return this.Balancesheet;
    }

    public void printName(){
        System.out.println(this.name);
    }

    public void printStockValue(){
        System.out.println(this.Balancesheet.phi);
    }

    public HedgefundBehaviour getBehaviour(){
        return this.Behaviour;
    }
}