using UnityEngine;
using System;
using System.Collections;
using System.IO;
using System.Collections.Generic;


public class GeneratorScript : MonoBehaviour {

    public Material def_material;
	public GameObject[] road_txtrs = null;
	public GameObject[] wasteland_txtrs = null;
	public GameObject[] test_txtrs = null;
	public GameObject[] game_obj_txtrs = null;
    public GameObject empty_cell = null;
    public int CELL_SIZE = 2;

	public Color cellSelectionColor;

    private int size_x = 0;
    private int size_y = 0;
    

	private Point startPoint, endPoint;
    public float map_size_x;
    public float map_size_y;

    private int[,] map = null;

	public Dictionary<int, Vector2> roadRoute = new Dictionary<int, Vector2>();
    public Queue<Vector2> route = new Queue<Vector2>();

	// Use this for initialization
	void Start () {
        if (Load())
        {
            map_size_x = endPoint.x - startPoint.x;
            map_size_y = endPoint.y - startPoint.y;

            Generate();
        }

	}

    private bool Generate() {
		/*
      	Дорога 1
		Пустыня 2
        Камень 3
        Вода 8
        Подземелье 9
        Рубидий 4
        Клетка башни 7
        Клетка старта 51
        Нексус 52
		*/
        Vector3 pos;
        GameObject ncell = null;
		System.Random rand = new System.Random(DateTime.Now.Millisecond);
		int roadCounter = 0;

		GameObject roadParent = GameObject.Find ("Road");
		GameObject decorationsParent = GameObject.Find ("Decorations");
		GameObject buildableParent = GameObject.Find ("Buildable");
		GameObject otherParent = GameObject.Find ("Other");

        for(int j = startPoint.y; j < endPoint.y; j++)
            for(int i = startPoint.x; i < endPoint.x; i++)
            {
				int txtr = map [j,i];

                pos = new Vector3((i - startPoint.x) * CELL_SIZE, (startPoint.y - j) * CELL_SIZE, 0);
				switch (txtr) {
				case 1: // road
                    roadRoute.Add(roadCounter++, new Vector2((i - startPoint.x) * CELL_SIZE, (startPoint.y - j) * CELL_SIZE));
					bool left, right, up, down;
					left = right = up = down = false;
					if (i + 1 < endPoint.x && map [j, i + 1] == 1) {
						right = true;
					}
					if (j + 1 < endPoint.y && map [j + 1, i] == 1) {
						down = true;
					}
					if (i - 1 >= startPoint.x && map [j, i - 1] == 1) {
						left = true;
					}
					if (j - 1 >= startPoint.y && map [j - 1, i] == 1) {
						up = true;
					}

					if (up && down)
						ncell = Instantiate (road_txtrs [rand.Next (0, 2)]) as GameObject;
					else if (up && right)
						ncell = Instantiate (road_txtrs [4]) as GameObject;
					else if (left && up)
						ncell = Instantiate (road_txtrs [5]) as GameObject;
					else if (left && down)
						ncell = Instantiate (road_txtrs [6]) as GameObject;
					else if (down && right)
						ncell = Instantiate (road_txtrs [7]) as GameObject;
					else
						ncell = Instantiate (road_txtrs [3]) as GameObject;

					ncell.transform.parent = roadParent.transform;
					break;
				case 2: // desert
					int txtr_id = rand.Next (0, 100);
					if (txtr_id >= 15) {
						if (txtr_id >= 35)
							txtr_id = 16;
						else
							txtr_id = 15;
					}
					ncell = Instantiate(wasteland_txtrs[txtr_id]) as GameObject;
					ncell.transform.parent = decorationsParent.transform;
					break;
				case 3: // stone
					ncell = Instantiate (wasteland_txtrs[17]) as GameObject;
					ncell.transform.parent = decorationsParent.transform;
					break;
				case 4: // рубидий
					ncell = Instantiate (game_obj_txtrs[2]) as GameObject;
					ncell.transform.parent = decorationsParent.transform;
					break;
				case 51:
                        ////
                    var spwn = GameObject.Find("SPAWNER");
                    spwn.GetComponent<Transform>().position = pos;
					ncell = Instantiate (test_txtrs[4]) as GameObject;
					ncell.transform.parent = otherParent.transform;
					//roadRoute.Add (roadCounter++, new Vector2(startPoint.y - j, i - startPoint.x) * CELL_SIZE);
                    roadRoute.Add(roadCounter++, new Vector2((i - startPoint.x) * CELL_SIZE, (startPoint.y - j) * CELL_SIZE));
					break;
				case 52:
					ncell = Instantiate (game_obj_txtrs[0]) as GameObject;
					ncell.transform.parent = otherParent.transform;
                    roadRoute.Add(roadCounter++, new Vector2((i - startPoint.x) * CELL_SIZE, (startPoint.y - j) * CELL_SIZE));
					break;
				case 7: // tower
					ncell = Instantiate (game_obj_txtrs[1]) as GameObject;
					ncell.transform.parent = buildableParent.transform;
					break;
				case 8: // water
					ncell = Instantiate (test_txtrs[7]) as GameObject;
					ncell.transform.parent = otherParent.transform;
					break;
				case 9: // underground
					ncell = Instantiate (test_txtrs[8]) as GameObject;
					ncell.transform.parent = otherParent.transform;
					break;
				default:
					ncell = Instantiate (empty_cell) as GameObject;
					ncell.transform.parent = otherParent.transform;
					break;
				}
				

            
				ncell.GetComponent<Transform>().transform.position = pos;
               
            }

        return true;
    }

    private bool Load() {
        string fileName;
        string line;
        if (ApplicationStatistics.GameType == 0)
        {
            fileName = ".\\Assets\\maps\\1.tdmap";
        }
        else
        {
            int fileNumber = new System.Random(System.DateTime.Now.Millisecond).Next(2, 7);
            fileName = ".\\Assets\\maps\\" + fileNumber + ".tdmap";
        }
        StreamReader theReader = new StreamReader(fileName);
        using (theReader) {   //field size
            line = theReader.ReadLine();
            if (line != null) {
                string[] entries = line.Split(' ');
                if (entries.Length > 0) {
                    size_x = int.Parse(entries[0]);
                    size_y = int.Parse(entries[1]);
                }; //so sad smile
            }
            //map start point
            line = theReader.ReadLine();
            if (line != null) {
                string[] entries = line.Split(' ');
                if (entries.Length > 0) {
					startPoint = new Point (int.Parse (entries [0]), int.Parse (entries [1]));
                };
            }
            //map end point
            line = theReader.ReadLine();
            if (line != null) {
                string[] entries = line.Split(' ');
                if (entries.Length > 0) {
					endPoint = new Point (int.Parse (entries [0]), int.Parse (entries [1]));
                };
            }
            map = new int[size_y, size_x];
            //cycle :)
            for (int j = 0; j < size_y; j++) {
                line = theReader.ReadLine();
                if (line != null) {
                    string[] entries = line.Split(' ');
                    if (entries.Length > 0) {
                        for (int i = 0; i < size_x; i++)
                            map[j, i] = int.Parse(entries[i]);
                    }
                }
            } //cycle ends
            /*
             * route style: 1:2 %->% 1:3 %->% 2:3
             * point x:y
             */
            line = theReader.ReadLine();
            if (line != null)
            {
                // route exists
                const String separator = "%->%";
                int next;
                while (line != null)
                {
                    next = line.IndexOf(separator);
                    try
                    {
                        if (next == -1)
                        {
                            route.Enqueue(ParseVector2(line));
                            break;
                        }
                        else
                        {
                            route.Enqueue(ParseVector2(line.Substring(0, next)));
                            line = line.Substring(next + separator.Length);
                        }
                    }
                    catch (Exception e)
                    {
                        print(e.Message);
                    }
                }
                if (route.Count == 0)
                {
                    // smth went wrong and route became null
                    GameObject.Find("COUNTER").GetComponent<CounterScript>().GameOver(false);
                    return false;
                }
            }
            else
            {
                // route was not found
                GameObject.Find("COUNTER").GetComponent<CounterScript>().GameOver(false);
                return false;
            }
            theReader.Close();
            return true;
		}     
	}//load() ends

	private class Point {
		public int x;
		public int y;

		public Point(int _x, int _y) {
			x = _x;
			y = _y;
		}
        public Point(String value)
        {
            Parse(value);
        }
        private void Parse(String value)
        {
            const String separator = ":";
            String[] values = value.Split(separator.ToCharArray());
            if (values.Length != 2)
                return;
            x = int.Parse(values[0]);
            y = int.Parse(values[1]);
        }
	}

    private Vector2 ParseVector2(String value)
    {
        const String separator = ":";
        String[] values = value.Split(separator.ToCharArray());
        if (values.Length != 2)
        {
            throw new Exception("Illegal value");
        }
        return new Vector2(int.Parse(values[0]), -int.Parse(values[1])) * CELL_SIZE;
    }
}
	