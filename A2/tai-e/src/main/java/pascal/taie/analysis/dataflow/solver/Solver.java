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
import pascal.taie.analysis.graph.cfg.CFG;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Base class for data-flow analysis solver, which provides common
 * functionalities for different solver implementations.
 *
 * @param <Node> type of CFG nodes
 * @param <Fact> type of data-flow facts
 */
public abstract class Solver<Node, Fact> {

    protected final DataflowAnalysis<Node, Fact> analysis;

    protected Solver(DataflowAnalysis<Node, Fact> analysis) {
        this.analysis = analysis;
    }

    /**
     * Static factory method to create a new solver for given analysis.
     */
    public static <Node, Fact> Solver<Node, Fact> makeSolver(
            DataflowAnalysis<Node, Fact> analysis) {
        return new WorkListSolver<>(analysis);
    }

    /**
     * Starts this solver on the given CFG.
     *
     * @param cfg control-flow graph where the analysis is performed on
     * @return the analysis result
     */
    public DataflowResult<Node, Fact> solve(CFG<Node> cfg) {
        DataflowResult<Node, Fact> result = initialize(cfg);
        doSolve(cfg, result);
        return result;
    }

    /**
     * Creates and initializes a new data-flow result for given CFG.
     *
     * @return the initialized data-flow result
     */
    private DataflowResult<Node, Fact> initialize(CFG<Node> cfg) {
        DataflowResult<Node, Fact> result = new DataflowResult<>();
        if (analysis.isForward()) {
            initializeForward(cfg, result);
        } else {
            initializeBackward(cfg, result);
        }
        return result;
    }

    protected void initializeForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        //BEGIN:OUT[Entry] = null;
        Node entryNode = cfg.getEntry();
        result.setOutFact(entryNode,analysis.newInitialFact());
        //END:OUT[Entry] = null;

        //BEGIN: foreach(basic block B : cfg - entryNode) OUT[B] = null;
        //transform the nodeSet to Stream and then again transform to List for later production of ListIterator
        List<Node> nodeSetList = cfg.getNodes().stream().toList();
        //Create ListIterator and set the Index as 1 because the entryNode has been dealt with
        ListIterator<Node> listIterator = nodeSetList.listIterator(1);
        //Check whether there is successor exist
        while(listIterator.hasNext()){
            //Initialize the Out_fact of each node to be emptySet except the entryNode
            Node node = listIterator.next();
            Fact Out_factOfNode = analysis.newInitialFact();
            result.setOutFact(node, Out_factOfNode);
            }
        }
        //END: foreach(basic block B : cfg - entryNode) OUT[B] = null;


    protected void initializeBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    /**
     * Solves the data-flow problem for given CFG.
     */
    private void doSolve(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        if (analysis.isForward()) {
            doSolveForward(cfg, result);
        } else {
            doSolveBackward(cfg, result);
        }
    }

    protected abstract void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result);

    protected abstract void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result);
}
