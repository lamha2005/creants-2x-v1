package com.creants.creants_2x.core.entities.match;

import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.QAntArray;

/**
 * @author LamHa
 *
 */
public class MatchExpression {
	private final String varName;
	private final IMatcher condition;
	private final Object value;
	private LogicOperator logicOp;
	private MatchExpression parent;
	private MatchExpression next;

	public MatchExpression(String varName, IMatcher condition, Object value) {
		this.logicOp = null;
		this.parent = null;
		this.next = null;
		this.varName = varName;
		this.condition = condition;
		this.value = value;
	}

	private MatchExpression(String varName, IMatcher condition, Object value, LogicOperator logicOp,
			MatchExpression parent) {
		this.logicOp = null;
		this.parent = null;
		this.next = null;
		this.varName = varName;
		this.condition = condition;
		this.value = value;
		this.logicOp = logicOp;
		this.parent = parent;
	}

	public MatchExpression and(String varName, IMatcher condition, Object value) {
		return next = new MatchExpression(varName, condition, value, LogicOperator.AND, this);
	}

	public MatchExpression or(final String varName, final IMatcher condition, final Object value) {
		return next = new MatchExpression(varName, condition, value, LogicOperator.OR, this);
	}

	String getVarName() {
		return varName;
	}

	IMatcher getCondition() {
		return condition;
	}

	Object getValue() {
		return value;
	}

	LogicOperator getLogicOp() {
		return logicOp;
	}

	@Override
	public String toString() {
		MatchExpression expr = this.rewind();
		StringBuilder sb = new StringBuilder(expr.asString());
		while (expr.hasNext()) {
			expr = expr.next();
			sb.append(expr.asString());
		}
		return sb.toString();
	}

	private String asString() {
		StringBuilder sb = new StringBuilder();
		if (logicOp != null) {
			sb.append(" ").append(this.logicOp).append(" ");
		}
		sb.append("(");
		sb.append(varName).append(" ").append(this.condition.getSymbol()).append(" ")
				.append((Object) ((this.value instanceof String) ? ("'" + this.value + "'") : this.value));
		sb.append(")");
		return sb.toString();
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public MatchExpression next() {
		return this.next;
	}

	public MatchExpression rewind() {
		MatchExpression currNode = this;
		for (int c = 0; currNode.parent != null; currNode = currNode.parent, ++c) {
		}
		return currNode;
	}

	public static MatchExpression fromSFSArray(IQAntArray sfsa) {
		MatchExpression expression = null;
		for (int i = 0; i < sfsa.size(); ++i) {
			IQAntArray expData = sfsa.getQAntArray(i);
			if (expData.size() != 5) {
				throw new IllegalArgumentException("Malformed expression data: " + sfsa.getDump());
			}

			LogicOperator logicOp = null;
			if (!expData.isNull(0)) {
				logicOp = LogicOperator.valueOf(expData.getUtfString(0));
			}

			String varName = expData.getUtfString(1);
			int matcherType = expData.getByte(2);
			IMatcher matcher = null;
			String matchSymbol = expData.getUtfString(3);
			if (matcherType == 0) {
				matcher = BoolMatch.fromSymbol(matchSymbol);
			} else if (matcherType == 1) {
				matcher = NumberMatch.fromSymbol(matchSymbol);
			} else {
				matcher = StringMatch.fromSymbol(matchSymbol);
			}
			final Object value = expData.getElementAt(4);
			if (logicOp == null) {
				expression = new MatchExpression(varName, matcher, value);
			} else if (logicOp == LogicOperator.AND) {
				expression = expression.and(varName, matcher, value);
			} else if (logicOp == LogicOperator.OR) {
				expression = expression.or(varName, matcher, value);
			}
		}
		return expression;
	}

	public IQAntArray toSFSArray() {
		MatchExpression expr = rewind();
		IQAntArray qanta = new QAntArray();
		qanta.addQAntArray(expr.expressionAsQAntArray());
		while (expr.hasNext()) {
			expr = expr.next();
			qanta.addQAntArray(expr.expressionAsQAntArray());
		}

		return qanta;
	}

	IQAntArray expressionAsQAntArray() {
		IQAntArray expr = new QAntArray();
		if (logicOp != null) {
			expr.addUtfString(logicOp.toString());
		} else {
			expr.addNull();
		}
		expr.addUtfString(varName);
		expr.addByte((byte) condition.getType());
		expr.addUtfString(condition.getSymbol());
		if (condition.getType() == 0) {
			expr.addBool((boolean) value);
		} else if (condition.getType() == 1) {
			expr.addDouble(((Number) value).doubleValue());
		} else {
			expr.addUtfString((String) value);
		}
		return expr;
	}
}
