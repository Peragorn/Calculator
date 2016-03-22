package com.firstproject.siaum.calculator;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class Expression {
    public static final BigDecimal PI = new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");
    public static final BigDecimal E = new BigDecimal("2.71828182846");
    private MathContext mc;
    private String expression;
    private List<String> rpn;
    private Map<String, Operator> operators;
    private Map<String, Expression.Function> functions;
    private Map<String, BigDecimal> variables;
    private static final char decimalSeparator = '.';
    private static final char minusSign = '-';
    private static final BigDecimal PARAMS_START = new BigDecimal(0);

    public Expression(String expression) {
        this(expression, MathContext.DECIMAL32);
    }

    public Expression(String expression, MathContext defaultMathContext) {
        this.mc = null;
        this.expression = null;
        this.rpn = null;
        this.operators = new HashMap();
        this.functions = new HashMap();
        this.variables = new HashMap();
        this.mc = defaultMathContext;
        this.expression = expression;
        this.addOperator(new Expression.Operator("+", 20, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.add(v2, Expression.this.mc);
            }
        });
        this.addOperator(new Expression.Operator("-", 20, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.subtract(v2, Expression.this.mc);
            }
        });
        this.addOperator(new Expression.Operator("*", 30, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.multiply(v2, Expression.this.mc);
            }
        });
        this.addOperator(new Expression.Operator("/", 30, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.divide(v2, Expression.this.mc);
            }
        });
        this.addOperator(new Expression.Operator("%", 30, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.remainder(v2, Expression.this.mc);
            }
        });
        this.addOperator(new Expression.Operator("^", 40, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                int signOf2 = v2.signum();
                double dn1 = v1.doubleValue();
                v2 = v2.multiply(new BigDecimal(signOf2));
                BigDecimal remainderOf2 = v2.remainder(BigDecimal.ONE);
                BigDecimal n2IntPart = v2.subtract(remainderOf2);
                BigDecimal intPow = v1.pow(n2IntPart.intValueExact(), Expression.this.mc);
                BigDecimal doublePow = new BigDecimal(Math.pow(dn1, remainderOf2.doubleValue()));
                BigDecimal result = intPow.multiply(doublePow, Expression.this.mc);
                if (signOf2 == -1) {
                    result = BigDecimal.ONE.divide(result, Expression.this.mc.getPrecision(), RoundingMode.HALF_UP);
                }

                return result;
            }
        });
        this.addOperator(new Expression.Operator("&&", 4, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                boolean b1 = !v1.equals(BigDecimal.ZERO);
                boolean b2 = !v2.equals(BigDecimal.ZERO);
                return b1 && b2 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("||", 2, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                boolean b1 = !v1.equals(BigDecimal.ZERO);
                boolean b2 = !v2.equals(BigDecimal.ZERO);
                return !b1 && !b2 ? BigDecimal.ZERO : BigDecimal.ONE;
            }
        });
        this.addOperator(new Expression.Operator(">", 10, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == 1 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator(">=", 10, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("<", 10, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == -1 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("<=", 10, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("=", 7, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("==", 7, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return ((Expression.Operator) Expression.this.operators.get("=")).eval(v1, v2);
            }
        });
        this.addOperator(new Expression.Operator("!=", 7, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addOperator(new Expression.Operator("<>", 7, false) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return ((Expression.Operator) Expression.this.operators.get("!=")).eval(v1, v2);
            }
        });
        this.addFunction(new Expression.Function("NOT", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                boolean zero = ((BigDecimal) parameters.get(0)).compareTo(BigDecimal.ZERO) == 0;
                return zero ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        this.addFunction(new Expression.Function("IF", 3) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                boolean isTrue = !((BigDecimal) parameters.get(0)).equals(BigDecimal.ZERO);
                return isTrue ? (BigDecimal) parameters.get(1) : (BigDecimal) parameters.get(2);
            }
        });
        this.addFunction(new Expression.Function("RANDOM", 0) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.random();
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("SIN", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.sin(Math.toRadians(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("COS", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.cos(Math.toRadians(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("TAN", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.tan(Math.toRadians(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("ASIN", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toDegrees(Math.asin(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("ACOS", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toDegrees(Math.acos(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("ATAN", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toDegrees(Math.atan(((BigDecimal) parameters.get(0)).doubleValue()));
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("SINH", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.sinh(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("COSH", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.cosh(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("TANH", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.tanh(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("RAD", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toRadians(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("DEG", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toDegrees(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("MAX", -1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                if (parameters.size() == 0) {
                    throw new Expression.ExpressionException("MAX requires at least one parameter");
                } else {
                    BigDecimal max = null;
                    Iterator i$ = parameters.iterator();

                    while (true) {
                        BigDecimal parameter;
                        do {
                            if (!i$.hasNext()) {
                                return max;
                            }

                            parameter = (BigDecimal) i$.next();
                        } while (max != null && parameter.compareTo(max) <= 0);

                        max = parameter;
                    }
                }
            }
        });
        this.addFunction(new Expression.Function("MIN", -1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                if (parameters.size() == 0) {
                    throw new Expression.ExpressionException("MIN requires at least one parameter");
                } else {
                    BigDecimal min = null;
                    Iterator i$ = parameters.iterator();

                    while (true) {
                        BigDecimal parameter;
                        do {
                            if (!i$.hasNext()) {
                                return min;
                            }

                            parameter = (BigDecimal) i$.next();
                        } while (min != null && parameter.compareTo(min) >= 0);

                        min = parameter;
                    }
                }
            }
        });
        this.addFunction(new Expression.Function("ABS", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                return ((BigDecimal) parameters.get(0)).abs(Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("LOG", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.log(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("LOG10", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.log10(((BigDecimal) parameters.get(0)).doubleValue());
                return new BigDecimal(d, Expression.this.mc);
            }
        });
        this.addFunction(new Expression.Function("ROUND", 2) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = (BigDecimal) parameters.get(0);
                int precision = ((BigDecimal) parameters.get(1)).intValue();
                return toRound.setScale(precision, Expression.this.mc.getRoundingMode());
            }
        });
        this.addFunction(new Expression.Function("FLOOR", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = (BigDecimal) parameters.get(0);
                return toRound.setScale(0, RoundingMode.FLOOR);
            }
        });
        this.addFunction(new Expression.Function("CEILING", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = (BigDecimal) parameters.get(0);
                return toRound.setScale(0, RoundingMode.CEILING);
            }
        });
        this.addFunction(new Expression.Function("SQRT", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal x = (BigDecimal) parameters.get(0);
                if (x.compareTo(BigDecimal.ZERO) == 0) {
                    return new BigDecimal(0);
                } else if (x.signum() < 0) {
                    throw new Expression.ExpressionException("Argument to SQRT() function must not be negative");
                } else {
                    BigInteger n = x.movePointRight(Expression.this.mc.getPrecision() << 1).toBigInteger();
                    int bits = n.bitLength() + 1 >> 1;
                    BigInteger ix = n.shiftRight(bits);

                    BigInteger ixPrev;
                    do {
                        ixPrev = ix;
                        ix = ix.add(n.divide(ix)).shiftRight(1);
                        Thread.yield();
                    } while (ix.compareTo(ixPrev) != 0);

                    return new BigDecimal(ix, Expression.this.mc.getPrecision());
                }
            }
        });
        this.addFunction(new Expression.Function("SILNIA", 1) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                int d = parameters.get(0).intValue();
                BigDecimal result = new BigDecimal("1");
                BigDecimal temp = new BigDecimal("1");

                do{
                    BigDecimal multiply = new BigDecimal(""+d);
                    result = result.multiply(multiply);
                } while (--d>0);
                return new BigDecimal(String.valueOf(result), Expression.this.mc);
            }
        });

        this.variables.put("PI", PI);
        this.variables.put("E", E);
        this.variables.put("TRUE", BigDecimal.ONE);
        this.variables.put("FALSE", BigDecimal.ZERO);
    }

    private boolean isNumber(String st) {
        if (st.charAt(0) == 45 && st.length() == 1) {
            return false;
        } else {
            char[] arr$ = st.toCharArray();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                char ch = arr$[i$];
                if (!Character.isDigit(ch) && ch != 45 && ch != 46) {
                    return false;
                }
            }

            return true;
        }
    }

    private List<String> shuntingYard(String expression) {
        ArrayList outputQueue = new ArrayList();
        Stack stack = new Stack();
        Expression.Tokenizer tokenizer = new Expression.Tokenizer(expression);
        String lastFunction = null;

        String element;
        for (String previousToken = null; tokenizer.hasNext(); previousToken = element) {
            element = tokenizer.next();
            if (this.isNumber(element)) {
                outputQueue.add(element);
            } else if (this.variables.containsKey(element)) {
                outputQueue.add(element);
            } else if (this.functions.containsKey(element.toUpperCase(Locale.ROOT))) {
                stack.push(element);
                lastFunction = element;
            } else if (Character.isLetter(element.charAt(0))) {
                stack.push(element);
            } else if (",".equals(element)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }

                if (stack.isEmpty()) {
                    throw new Expression.ExpressionException("Parse error for function \'" + lastFunction + "\'");
                }
            } else if (this.operators.containsKey(element)) {
                Expression.Operator o1 = (Expression.Operator) this.operators.get(element);

                for (String token2 = stack.isEmpty() ? null : (String) stack.peek(); this.operators.containsKey(token2) && (o1.isLeftAssoc() && o1.getPrecedence() <= ((Expression.Operator) this.operators.get(token2)).getPrecedence() || o1.getPrecedence() < ((Expression.Operator) this.operators.get(token2)).getPrecedence()); token2 = stack.isEmpty() ? null : (String) stack.peek()) {
                    outputQueue.add(stack.pop());
                }

                stack.push(element);
            } else if ("(".equals(element)) {
                if (previousToken != null) {
                    if (this.isNumber(previousToken)) {
                        throw new Expression.ExpressionException("Missing operator at character position " + tokenizer.getPos());
                    }

                    if (this.functions.containsKey(previousToken.toUpperCase(Locale.ROOT))) {
                        outputQueue.add(element);
                    }
                }

                stack.push(element);
            } else if (")".equals(element)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }

                if (stack.isEmpty()) {
                    throw new RuntimeException("Mismatched parentheses");
                }

                stack.pop();
                if (!stack.isEmpty() && this.functions.containsKey(((String) stack.peek()).toUpperCase(Locale.ROOT))) {
                    outputQueue.add(stack.pop());
                }
            }
        }

        while (!stack.isEmpty()) {
            element = (String) stack.pop();
            if ("(".equals(element) || ")".equals(element)) {
                throw new RuntimeException("Mismatched parentheses");
            }

            if (!this.operators.containsKey(element)) {
                throw new RuntimeException("Unknown operator or function: " + element);
            }

            outputQueue.add(element);
        }

        return outputQueue;
    }

    public BigDecimal eval() {
        Stack stack = new Stack();
        Iterator i$ = this.getRPN().iterator();

        while (true) {
            while (i$.hasNext()) {
                String token = (String) i$.next();
                if (this.operators.containsKey(token)) {
                    BigDecimal f1 = (BigDecimal) stack.pop();
                    BigDecimal p1 = (BigDecimal) stack.pop();
                    stack.push(((Expression.Operator) this.operators.get(token)).eval(p1, f1));
                } else if (this.variables.containsKey(token)) {
                    stack.push(((BigDecimal) this.variables.get(token)).round(this.mc));
                } else if (!this.functions.containsKey(token.toUpperCase(Locale.ROOT))) {
                    if ("(".equals(token)) {
                        stack.push(PARAMS_START);
                    } else {
                        stack.push(new BigDecimal(token, this.mc));
                    }
                } else {
                    Expression.Function f = (Expression.Function) this.functions.get(token.toUpperCase(Locale.ROOT));
                    ArrayList p = new ArrayList(!f.numParamsVaries() ? f.getNumParams() : 0);

                    while (!stack.isEmpty() && stack.peek() != PARAMS_START) {
                        p.add(0, stack.pop());
                    }

                    if (stack.peek() == PARAMS_START) {
                        stack.pop();
                    }

                    if (!f.numParamsVaries() && p.size() != f.getNumParams()) {
                        throw new Expression.ExpressionException("Function " + token + " expected " + f.getNumParams() + " parameters, got " + p.size());
                    }

                    BigDecimal fResult = f.eval(p);
                    stack.push(fResult);
                }
            }

            return ((BigDecimal) stack.pop()).stripTrailingZeros();
        }
    }

    public Expression setPrecision(int precision) {
        this.mc = new MathContext(precision);
        return this;
    }

    public Expression setRoundingMode(RoundingMode roundingMode) {
        this.mc = new MathContext(this.mc.getPrecision(), roundingMode);
        return this;
    }

    public Expression.Operator addOperator(Expression.Operator operator) {
        return (Expression.Operator) this.operators.put(operator.getOper(), operator);
    }

    public Expression.Function addFunction(Expression.Function function) {
        return (Expression.Function) this.functions.put(function.getName(), function);
    }

    public Expression setVariable(String variable, BigDecimal value) {
        this.variables.put(variable, value);
        return this;
    }

    public Expression setVariable(String variable, String value) {
        if (this.isNumber(value)) {
            this.variables.put(variable, new BigDecimal(value));
        } else {
            this.expression = this.expression.replaceAll("\\b" + variable + "\\b", "(" + value + ")");
            this.rpn = null;
        }

        return this;
    }

    public Expression with(String variable, BigDecimal value) {
        return this.setVariable(variable, value);
    }

    public Expression and(String variable, String value) {
        return this.setVariable(variable, value);
    }

    public Expression and(String variable, BigDecimal value) {
        return this.setVariable(variable, value);
    }

    public Expression with(String variable, String value) {
        return this.setVariable(variable, value);
    }

    public Iterator<String> getExpressionTokenizer() {
        return new Expression.Tokenizer(this.expression);
    }

    private List<String> getRPN() {
        if (this.rpn == null) {
            this.rpn = this.shuntingYard(this.expression);
            this.validate(this.rpn);
        }

        return this.rpn;
    }

    private void validate(List<String> rpn) {
        int counter = 0;
        Stack params = new Stack();

        for (Iterator i$ = rpn.iterator(); i$.hasNext(); ++counter) {
            String token = (String) i$.next();
            if ("(".equals(token)) {
                if (!params.isEmpty()) {
                    params.set(params.size() - 1, Integer.valueOf(((Integer) params.peek()).intValue() + 1));
                }

                params.push(Integer.valueOf(0));
            } else if (!params.isEmpty()) {
                if (this.functions.containsKey(token.toUpperCase(Locale.ROOT))) {
                    counter -= ((Integer) params.pop()).intValue() + 1;
                } else {
                    params.set(params.size() - 1, Integer.valueOf(((Integer) params.peek()).intValue() + 1));
                }
            } else if (this.operators.containsKey(token)) {
                counter -= 2;
            }

            if (counter < 0) {
                throw new Expression.ExpressionException("Too many operators or functions at: " + token);
            }
        }

        if (counter > 1) {
            throw new Expression.ExpressionException("Too many numbers or variables");
        } else if (counter < 1) {
            throw new Expression.ExpressionException("Empty expression");
        }
    }

    public String toRPN() {
        StringBuilder result = new StringBuilder();

        String st;
        for (Iterator i$ = this.getRPN().iterator(); i$.hasNext(); result.append(st)) {
            st = (String) i$.next();
            if (result.length() != 0) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    private class Tokenizer implements Iterator<String> {
        private int pos = 0;
        private String input;
        private String previousToken;

        public Tokenizer(String input) {
            this.input = input.trim();
        }

        public boolean hasNext() {
            return this.pos < this.input.length();
        }

        private char peekNextChar() {
            return this.pos < this.input.length() - 1 ? this.input.charAt(this.pos + 1) : '\u0000';
        }

        public String next() {
            StringBuilder token = new StringBuilder();
            if (this.pos >= this.input.length()) {
                return this.previousToken = null;
            } else {
                char ch;
                for (ch = this.input.charAt(this.pos); Character.isWhitespace(ch) && this.pos < this.input.length(); ch = this.input.charAt(++this.pos)) {
                    ;
                }

                if (Character.isDigit(ch)) {
                    while ((Character.isDigit(ch) || ch == 46) && this.pos < this.input.length()) {
                        token.append(this.input.charAt(this.pos++));
                        ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                    }
                } else if (ch == 45 && Character.isDigit(this.peekNextChar()) && ("(".equals(this.previousToken) || ",".equals(this.previousToken) || this.previousToken == null || Expression.this.operators.containsKey(this.previousToken))) {
                    token.append('-');
                    ++this.pos;
                    token.append(this.next());
                } else if (!Character.isLetter(ch) && ch != 95) {
                    if (ch != 40 && ch != 41 && ch != 44) {
                        while (!Character.isLetter(ch) && !Character.isDigit(ch) && ch != 95 && !Character.isWhitespace(ch) && ch != 40 && ch != 41 && ch != 44 && this.pos < this.input.length()) {
                            token.append(this.input.charAt(this.pos));
                            ++this.pos;
                            ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                            if (ch == 45) {
                                break;
                            }
                        }

                        if (!Expression.this.operators.containsKey(token.toString())) {
                            throw new Expression.ExpressionException("Unknown operator \'" + token + "\' at position " + (this.pos - token.length() + 1));
                        }
                    } else {
                        token.append(ch);
                        ++this.pos;
                    }
                } else {
                    while ((Character.isLetter(ch) || Character.isDigit(ch) || ch == 95) && this.pos < this.input.length()) {
                        token.append(this.input.charAt(this.pos++));
                        ch = this.pos == this.input.length() ? 0 : this.input.charAt(this.pos);
                    }
                }

                return this.previousToken = token.toString();
            }
        }

        public void remove() {
            throw new Expression.ExpressionException("remove() not supported");
        }

        public int getPos() {
            return this.pos;
        }
    }

    public abstract class Operator {
        private String oper;
        private int precedence;
        private boolean leftAssoc;

        public Operator(String oper, int precedence, boolean leftAssoc) {
            this.oper = oper;
            this.precedence = precedence;
            this.leftAssoc = leftAssoc;
        }

        public String getOper() {
            return this.oper;
        }

        public int getPrecedence() {
            return this.precedence;
        }

        public boolean isLeftAssoc() {
            return this.leftAssoc;
        }

        public abstract BigDecimal eval(BigDecimal var1, BigDecimal var2);
    }

    public abstract class Function {
        private String name;
        private int numParams;

        public Function(String name, int numParams) {
            this.name = name.toUpperCase(Locale.ROOT);
            this.numParams = numParams;
        }

        public String getName() {
            return this.name;
        }

        public int getNumParams() {
            return this.numParams;
        }

        public boolean numParamsVaries() {
            return this.numParams < 0;
        }

        public abstract BigDecimal eval(List<BigDecimal> var1);
    }

    public static class ExpressionException extends RuntimeException {
        private static final long serialVersionUID = 1118142866870779047L;

        public ExpressionException(String message) {
            super(message);
        }
    }
}
