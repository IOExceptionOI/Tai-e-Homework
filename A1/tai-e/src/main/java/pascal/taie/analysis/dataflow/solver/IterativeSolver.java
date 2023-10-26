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

package pascal.taie.analysis.dataflow.solver;

import pascal.taie.analysis.dataflow.analysis.DataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.ir.exp.Var;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

class IterativeSolver<Node, Fact> extends Solver<Node, Fact> {

    public IterativeSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        //transform the nodeSet to Stream and then again transform to List for later production of ListIterator
        List<Node> nodeList = cfg.getNodes().stream().toList();
        //Create ListIterator and set the Index as size() - 1 because the exitNode has been dealt with
        //Initialize the changeFlag as false, which indicates whether any of the basic block is changed
        boolean changeFlag = false;

        //BEGIN: do - while:
        do{
            changeFlag = false;
            ListIterator<Node> listIterator = nodeList.listIterator(nodeList.size() - 1);
            //Check whether there is predecessor exist
            while(listIterator.hasPrevious()){
                //obtain the node to be process currently
                Node curNode = listIterator.previous();
                //obtain the Set of successors of current node
                Set<Node> sucSetOfCurNode = cfg.getSuccsOf(curNode);
                //Initialize the unionResult to be emptySet
                Fact unionResult = analysis.newInitialFact();

                //BEGIN: for-loop
                for(Node node : sucSetOfCurNode){
                    //Create a variable to store the In_fact of each successor node
                    Fact IN_factOfEachNode = analysis.newInitialFact();
                    //store the In_fact of each successor node
                    IN_factOfEachNode =  result.getInFact(node);
                    //Method meetInto require that the two para should not be null
                    if(unionResult != null && IN_factOfEachNode != null){
                        //union each In_fact of successor node into the unionResult
                        analysis.meetInto(IN_factOfEachNode,unionResult);
                    }
                }
                //END: for-loop
                //set the Out_fact of current node to be the unionResult
                result.setOutFact(curNode,unionResult);
                //obtain the In_fact of current node
                Fact IN_factOfCurNode = result.getInFact(curNode);
                //obtain the Out_fact of current node
                Fact OUT_factOfCurNode = result.getOutFact(curNode);
                //Method analysis.transferNode() return a boolean indicating whether the
                //transform of current node make the In_fact different
                // for determining whether the do-while loop need to be stopped
                // (through setting the changeFlag to be true when any In_fact of basic block changes)
                if(analysis.transferNode(curNode,IN_factOfCurNode,OUT_factOfCurNode)){
                    result.setInFact(curNode,IN_factOfCurNode);
                    result.setOutFact(curNode,OUT_factOfCurNode);
                    changeFlag = true;
                }
            }
        }while(changeFlag);
        //END: do - while
    }
}
