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

    private int size_x = 0;
    private int size_y = 0;

	private Point startPoint, endPoint;

    private int[,] map = null;

	public Dictionary<int, int[]> roadRoute = new Dictionary<int, int[]>();

	// Use this for initialization
	void Start () {

		string filename = ".\\Assets\\maps\\example.tdmap";
        Load(filename);
        Generate();

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
        for(int j = startPoint.y; j < endPoint.y; j++)
            for(int i = startPoint.x; i < endPoint.x; i++)
            {
				int txtr = map [j,i];

                pos = new Vector3((i - startPoint.x) * CELL_SIZE, (startPoint.y - j) * CELL_SIZE, 0);
				switch (txtr) {
				case 1: // road
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
					//else if (left && right) ncell = Instantiate (road_txtrs[3]) as GameObject;
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
					roadRoute.Add (roadCounter++, new int[]{ startPoint.y - j, i - startPoint.x });
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
					break;
				case 3: // stone
					ncell = Instantiate (wasteland_txtrs[17]) as GameObject;
					break;
				case 4: // рубидий
					ncell = Instantiate (game_obj_txtrs[2]) as GameObject;
					break;
				case 51:
                        ////
                    var spwn = GameObject.Find("SPAWNER");
                    spwn.GetComponent<Transform>().position = pos;
					ncell = Instantiate (test_txtrs[4]) as GameObject;
					roadRoute.Add (roadCounter++, new int[]{ startPoint.y - j, i - startPoint.x });
					break;
				case 52:
					ncell = Instantiate (game_obj_txtrs[0]) as GameObject;
					roadRoute.Add (roadCounter++, new int[]{ startPoint.y - j, i - startPoint.x });
					break;
				case 7: // tower
					ncell = Instantiate (game_obj_txtrs[1]) as GameObject;
					break;
				case 8:
					ncell = Instantiate (test_txtrs[7]) as GameObject;
					break;
				case 9:
					ncell = Instantiate (test_txtrs[8]) as GameObject;
					break;
				default:
					ncell = Instantiate (empty_cell) as GameObject;
					break;
				}
				

            
				ncell.GetComponent<Transform>().transform.position = pos;
               
            }

        return true;
    }

    private bool Load(string fileName) {      
        string line;       
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
            }   
            theReader.Close();
            return true;
		} //cycle ends      
	}//load() ends

	private class Point {
		public int x;
		public int y;

		public Point(int _x, int _y) {
			x = _x;
			y = _y;
		}
	}
}
	