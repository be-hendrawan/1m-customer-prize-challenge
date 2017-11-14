# Redmart 1,000,000th Customer Prize Challenge

This is my solution for the [challenge](http://geeks.redmart.com/2015/10/26/1000000th-customer-prize-another-programming-challenge/). The challenge is largely the same as a classic 0-1 knapsack problem, but with some minor rule changes and a large dataset to play with.

## Problem
- 1 of each item
- Combined volume < tote capacity (45 * 30 * 25 = 47250)
- Item must fit individually (Dimensions are such that it can fit into the tote)
- **Maximize** value of combined products
- **Minimize** weight on draws

## Solution
I use Knapsack algorithm from [this web](http://rerun.me/2014/05/27/the-knapsack-problem/) and modify the logic to match with the challenge. Due to the large amount of data, I keep hitting java heap memory error. Due to that, I to tune up few things to make it work in my workstation.
- Reduce the size of the knapsack array. Original algorithm required array column as much as the tote capacity. But since column with index lesser than the smallest item volume are only populated with zero, I truncated the number of rows to tote capacity - smallest item volume.
- Free up memory for rows that we don't use. Knapsack algorithm require array row as much as the number of item. But since the algorithm actually only need to look at row-1, so once we reach row 3, row 1 is no longer needed. I free up by null-ing the content and it significantly improve the runtime. Total runtime needed is around 1 minute 36 seconds.
