# Explanation

I decided to separate the logic of building a graph of all possible execution paths and a symbolic
execution tree.

I called the first graph Intermediate Representation. It's already very similar to
the desired graph, except it doesn't keep track of `symbolicStore` and `pathConstraints`. A node of
this graph can potentially hold any expression, but since in this case we need to get a graph of all
execution paths, I recursively propagate all the nodes with the `propagate()` method. Potentially it
is
possible to change the code so that only certain nodes are propagated to save time and memory.

The building of the symbolic execution after that is very straightforward: just walk over the
intermediate representation and recalculate the `symbolicStore` and `pathConstraints` properties
for each node incrementally. Although another data structure would be more fitting to represent
`symbolicStorage`, I decided to stick with `List<Expr>` to focus on other aspects of the task.
The same reasoning is behind not providing meaningful exceptions and their handling in case of
errors.