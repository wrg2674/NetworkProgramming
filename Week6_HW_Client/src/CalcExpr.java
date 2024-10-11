// 1971135 정구현
import java.io.Serializable;

public class CalcExpr implements Serializable{
	double operand1;
	double operand2;
	char operator;
	CalcExpr(double operand1, char operator, double operand2){
		this.operand1=operand1;
		this.operator = operator;
		this.operand2 = operand2;
	}
}
