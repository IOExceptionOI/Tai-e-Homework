/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.analysis.constprop;

import pascal.taie.analysis.dataflow.analysis.AbstractDataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.MapFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.*;
import pascal.taie.ir.stmt.DefinitionStmt;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.language.type.PrimitiveType;
import pascal.taie.language.type.Type;
import pascal.taie.util.AnalysisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstantPropagation extends
        AbstractDataflowAnalysis<Stmt, CPFact> {

    public static final String ID = "constprop";

    public ConstantPropagation(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public CPFact newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        return new CPFact();
    }

    @Override
    public CPFact newInitialFact() {
        // TODO - finish me
        return new CPFact();
    }

    @Override
    public void meetInto(CPFact fact, CPFact target) {
        // TODO - finish me
        if(fact != null && target != null){
            //obtain the Var Set of the target
            Set<Var> targetVarSet = target.keySet();
            for(Var targetVar : targetVarSet){
                Value v_1 = fact.get(targetVar);
                Value v_2 = target.get(targetVar);
                Value meetResult = meetValue(v_1, v_2);
                target.update(targetVar, meetResult);
            }
        }
    }

    /**
     * Meets two Values.
     */
    public Value meetValue(Value v1, Value v2) {
        // TODO - finish me
        //rule1: (x, v) ∧ (x, NAC) = (x, NAC)
        //identity to: either v_1 or v_2 is NAC
        if(v1.isNAC() || v2.isNAC()){
            return Value.getNAC();
        }
        //rule2: (x, UNDEF) ∧ (x, v) = (x, v) ---> In this case, we don't care about the problem caused by UNDEF
        if(v1.isConstant() && v2.isUndef() || v1.isUndef() && v2.isConstant()){
            if(v1.isConstant()){
                return Value.makeConstant(v1.getConstant());
            }else{
                return Value.makeConstant(v2.getConstant());
            }
        }
        //rule3: (x, v_1) ∧ (x, v_2) = [1: (x, v_1) if v1 == v2]  [2:(x, NAC) if v_1 != v_2]
        if(v1.isConstant() && v2.isConstant()){
            if(v1.getConstant() == v2.getConstant()){
                return v1;
            }else{
                return Value.getNAC();
            }
        }
        return null;
    }

    @Override
    public boolean transferNode(Stmt stmt, CPFact in, CPFact out) {
        // TODO - finish me
        MapFact<Var, Value> gens = new CPFact();
        DefinitionStmt<?,?> definitionStmt  = null;
        if(stmt instanceof DefinitionStmt<?, ?>){
            definitionStmt = (DefinitionStmt<?, ?>) stmt;
        }else{
            //do nothing if the stmt is not a definitionStmt
            //return false to indicate that this basic block do not change after this transfer
            return false;
        }
        LValue lValue = definitionStmt.getLValue();
        RValue rValue = definitionStmt.getRValue();


        //definitionStmt.getLValue();


        return false;
    }

    /**
     * @return true if the given variable can hold integer value, otherwise false.
     */
    public static boolean canHoldInt(Var var) {
        Type type = var.getType();
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BYTE:
                case SHORT:
                case INT:
                case CHAR:
                case BOOLEAN:
                    return true;
            }
        }
        return false;
    }

    /**
     * Evaluates the {@link Value} of given expression.
     *
     * @param exp the expression to be evaluated
     * @param in  IN fact of the statement
     * @return the resulting {@link Value}
     */
    public static Value evaluate(Exp exp, CPFact in) {
        // TODO - finish me
        List<RValue> expUses = exp.getUses();
        for(RValue rValue : expUses){
           // if(rValue instanceof )
        }
        //rule1: s: x = c; c is a constant ,则 gens = {(x, c)} ；

        //rule2: s: x = y; gens={(x, val(y))};

        //rule3: s: x = y op z; gens = {x, f(y,z)}
        //cases of f(y, z):
        //case1: f(y, z) = [val(y) op val(z)] if y and z are all constant

        //case2: f(y, z) = [NAC] if either y or z is NAC

        //case3: f(y, z) = UNDEF otherwise

        return null;
    }
}
