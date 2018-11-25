package app.mediabrainz.api.search;

import java.util.ArrayList;
import java.util.List;

import app.mediabrainz.api.search.SearchServiceInterface.SearchFieldInterface;

import static app.mediabrainz.api.search.LuceneBuilder.Operator.AND;
import static app.mediabrainz.api.search.LuceneBuilder.Operator.NOT;
import static app.mediabrainz.api.search.LuceneBuilder.Operator.OR;


public class LuceneBuilder {

    public enum Operator {
        AND("AND"),
        OR("OR"),
        NOT("NOT");

        private final String operator;
        Operator(String operator) {
            this.operator = operator;
        }
        @Override
        public String toString() {
            return operator;
        }
    }

    public static class Term {
        private boolean isOperator;
        private Operator operator;
        private SearchFieldInterface paramType;
        private String value;

        public Term(Operator operator) {
            this.operator = operator;
            isOperator = true;
        }

        public Term(SearchFieldInterface paramType, String value) {
            this.paramType = paramType;
            this.value = value;
        }

        public static Term and() {
            return new Term(AND);
        }

        public static Term or() {
            return new Term(OR);
        }

        public static Term not() {
            return new Term(NOT);
        }


        public boolean isOperator() {
            return isOperator;
        }

        public Operator getOperator() {
            return operator;
        }

        public SearchFieldInterface getParamType() {
            return paramType;
        }

        public String getValue() {
            return value;
        }
    }

    private List<Term> terms = new ArrayList<>();

    public LuceneBuilder() {
    }

    // TODO: add exceptions?
    public LuceneBuilder add(Operator operator) {
        int size = terms.size();
        // 0: or param / and param
        if (size == 0 && !operator.equals(NOT)) {
            //exception
            return this;
        }
        if (size > 0) {
            Term lastTerm = terms.get(size - 1);
            // or or / and and / not not
            // or and / and or
            // not and / not or
            if (lastTerm.isOperator() && (lastTerm.getOperator().equals(operator) || !operator.equals(NOT))) {
                //exception
                return this;
            }
            // param not param >> param and not param
            if (operator.equals(NOT) && !lastTerm.isOperator()) {
                terms.add(Term.and());
            }
        }
        terms.add(new Term(operator));
        return this;
    }

    public LuceneBuilder add(SearchFieldInterface paramType, String value) {
        int size = terms.size();
        // param param -> param and param
        if (size > 0 && !terms.get(size - 1).isOperator()) {
            terms.add(Term.and());
        }
        terms.add(new Term(paramType, value));
        return this;
    }

    public LuceneBuilder add(String value) {
        if (terms.isEmpty()) {
            terms.add(new Term(null, value));
        }
        return this;
    }

    /**
     * @return String : "artist:fred AND type:group AND country:US" or "fred AND type:group AND country:US"
     */
    public String build() {
        StringBuilder sb = new StringBuilder(" ");
        if (!terms.isEmpty() && !terms.get(0).isOperator && terms.get(0).getParamType() == null) {
            sb.append(terms.remove(0).getValue()).append(" ");
        }
        for (Term term : terms) {
            if (term.isOperator) {
                sb.append(term.getOperator());
            } else {
                sb.append(term.getParamType()).append(":").append(term.getValue());
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
