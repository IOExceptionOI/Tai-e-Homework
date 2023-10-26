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

package pascal.taie.analysis.dataflow.analysis;

import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.exp.LValue;
import pascal.taie.ir.exp.RValue;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Stmt;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of classic live variable analysis.
 */
public class LiveVariableAnalysis extends
        AbstractDataflowAnalysis<Stmt, SetFact<Var>> {

    public static final String ID = "livevar";

    public LiveVariableAnalysis(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public SetFact<Var> newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        //return the emptySet for initializing
        return  new SetFact<>();
    }

    @Override
    public SetFact<Var> newInitialFact() {

        // TODO - finish me
        //return the emptySet for initializing
        return new SetFact<>();
    }

    @Override
    public void meetInto(SetFact<Var> fact, SetFact<Var> target) {
        // TODO - finish me
        //for over-approximation, the meet operator should be union
        SetFact<Var> unionSetFactResult = target.unionWith(fact);
        target.set(unionSetFactResult);
        //aaa
    }

    @Override
    public boolean transferNode(Stmt stmt, SetFact<Var> in, SetFact<Var> out) {
        // TODO - finish me
        //First step : get the def-var in the stmt
        Optional<LValue> def =  stmt.getDef();
        //create the Fact-Set to store the def-var
        SetFact<Var> defVarSetFact = new SetFact<>();

        //check the existence of the def-var
        if(def.isPresent()){
            //obtain the def-var
            LValue defVar = def.get();
            //check whether the def-var is an instance of Var
            //because the defVar belongs to the child Class : LValue
            // which extends the father Class : Var
            if(defVar instanceof  Var){
                // if defVar is an instance of Var, then add the def-var to the Fact-Set
                defVarSetFact.add((Var)defVar);
            }
        }

        //obtain the RValues(uses) in the stmt
        List<RValue> uses = stmt.getUses();
        //create a Fact-Set for store use-var
        SetFact<Var> rValueSetFact = new SetFact<>();
        //check whether the RValue List is empty
        if(!uses.isEmpty()){
            //iterate all RValues in the uses to obtain the use-var
            for(RValue rvalue : uses){
                //check whether the RValue is an instance of Var
                //because the rvalue belongs to the child Class : RValue
                // which extends the father Class : Var
                if(rvalue instanceof Var){
                    // if the rvalue is an instance of Var, then add it to the Fact-Set
                    rValueSetFact.add((Var)rvalue);
                }
            }
        }
        //obtain the kills-Set
        SetFact<Var> kills = new SetFact<>();

        //Definition of kills:
        //kills = Out[B] - Def[B]
        //initialize the kills as a copy of out
        kills = out.copy();

        //BEGIN : //kills = Out[B] - Def[B]

        for(LValue defVar : defVarSetFact.stream().toList()){
            if(defVar instanceof  Var){
                kills.remove((Var)defVar);
            }
        }

        //END : //kills = Out[B] - Def[B]

        //Definition of gen:
        //gen = use-var
        //set the gen as the use-var
        SetFact<Var> gen = new SetFact<>();
        gen = rValueSetFact;

        //judge whether the In_fact of this basic block changes after the transfer
        //First step: make a deep-copy for In_fact of this basic block
        SetFact<Var> inCopy = new SetFact<>();
        inCopy.set(in);
        //Second step: store the result of gen[B] U kills[B]
        SetFact<Var> result = new SetFact<>();
        meetInto(gen, result);
        meetInto(kills, result);
        //update the In_fact of this basic block
        in.set(result);
        //Third step: compare the transferred In_fact to the original In_fact
        if(result.equals(inCopy)){
            //if nothing changed,
            //return false to indicate the changeFlag should be false
            return false;
        }else {
            //if something changed,
            //return true to indicate the changeFlag should be true to continue the do-while loop
            return true;
        }
    }
}
