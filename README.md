# Adversarial search agent for the game Lines of Action!

A Student project made by David Stankovski and Jakov Damjanski. 
In this project we have a fully playable implementation of the game and an agent that employs
many of the well known search techniques generally used in adversarial search.

You can download the latest version of our application [here](https://www.dropbox.com/scl/fi/qw1nck0dm8bpd40pema3q/LinesOfAction.jar?rlkey=zct0abwkfzyfzfjqmlvxd7rmx&dl=0 "Download app").
**(NOTE:  you need java 19 or later to run the game)**

Bellow we have displayed the data gathered from a series of simulations demonstrating the effects the employed techniques
have on our agent.

Our source data neatly presented and organised can be found [here](https://finkiukim-my.sharepoint.com/:x:/g/personal/david_stankovski_students_finki_ukim_mk/EXHHZ6FbRmhKh9WW7E2L2AcBmHmEsamCrC289FRQLWpRXw?e=cKWfRN "Go to data sheets").

## Results obtained from simulating two AI agents with different evaluation functions at same depth
<div>
<h4>Results from simulation 1:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1                     |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0         | 1        | 2        |
| --------------------------------- | --------- | -------- | -------- |
| Games played:                     | 200       | 200      | 200      |
| Games won by white:               | 74        | 75       | 62       |
| Games won by black:               | 126       | 125      | 138      |
| White AI win percentage:          | 37.00%    | 37.50%   | 31.00%   |
| Black AI win percentage:          | 63.00%    | 62.50%   | 69.00%   |
| Average Number of Moves per Game: | 25.08     | 85.475   | 32.755   |

<hr>
<h4>Results from simulation 2:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1, h2                 |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0         | 1       | 2        |
| --------------------------------- | --------- | ------- | -------- |
| Games played:                     | 200       | 200     | 200      |
| Games won by white:               | 84        | 100     | 121      |
| Games won by black:               | 116       | 100     | 79       |
| White AI win percentage:          | 42.00%    | 50.00%  | 60.50%   |
| Black AI win percentage:          | 58.00%    | 50.00%  | 39.50%   |
| Average Number of Moves per Game: | 25.89     | 92.755  | 31.93    |

<hr>
<h4>Results from simulation 3:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1, h2, h3             |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0        | 1        | 2        |
| --------------------------------- | -------- | -------- | -------- |
| Games played:                     | 200      | 200      | 200      |
| Games won by white:               | 128      | 104      | 124      |
| Games won by black:               | 72       | 96       | 76       |
| White AI win percentage:          | 64.00%   | 52.00%   | 62.00%   |
| Black AI win percentage:          | 36.00%   | 48.00%   | 38.00%   |
| Average Number of Moves per Game: | 26.48    | 91.925   | 29.72    |

<hr>
<h4>Results from simulation 4:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1, h2, h3, h4         |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0         | 1         | 2         |
| --------------------------------- | --------- | --------- | --------- |
| Games played:                     | 200       | 200       | 200       |
| Games won by white:               | 33        | 91        | 111       |
| Games won by black:               | 167       | 109       | 89        |
| White AI win percentage:          | 16.50%    | 45.50%    | 55.50%    |
| Black AI win percentage:          | 83.50%    | 54.50%    | 44.50%    |
| Average Number of Moves per Game: | 29.315    | 107.51    | 30.655    |

<hr>
<h4>Results from simulation 5:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1, h2, h3, h4, h5     |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0        | 1         | 2         |
| --------------------------------- | -------- | --------- | --------- |
| Games played:                     | 200      | 200       | 200       |
| Games won by white:               | 72       | 96        | 133       |
| Games won by black:               | 128      | 104       | 67        |
| White AI win percentage:          | 36.00%   | 48.00%    | 66.50%    |
| Black AI win percentage:          | 64.00%   | 52.00%    | 33.50%    |
| Average Number of Moves per Game: | 23.33    | 110.665   | 30.955    |

<hr>
<h4>Results from simulation 6:</h4>

| AI Color: | Evaluation functions:  |
| --------- | ---------------------- |
| White:    | h1, h2, h3, h4, h5, h6 |
| Black:    | h1, h2, h3, h4, h5, h6 |

| Depth:                            | 0        | 1         | 2         |
| --------------------------------- | -------- | --------- | --------- |
| Games played:                     | 200      | 200       | 200       |
| Games won by white:               | 102      | 101       | 109       |
| Games won by black:               | 98       | 99        | 91        |
| White AI win percentage:          | 51.00%   | 50.50%    | 54.50%    |
| Black AI win percentage:          | 49.00%   | 49.50%    | 45.50%    |
| Average Number of Moves per Game: | 22.96    | 111.945   | 34.3      |
</div>
